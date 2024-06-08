(ns moviefinder-app.movie.movie-db-impl-tmdb
  (:require [clj-http.client :as client]
            [clojure.set :refer [rename-keys]]
            [moviefinder-app.movie.movie-db :as movie-db]
            [moviefinder-app.env :as env]))

;; 
;; 
;; 
;; 
;; 
;; 

(def cache! (atom {}))

;; 
;; 
;; 
;; 
;; 
;; 
;; 

(def api-read-access-token (env/get! "TMDB_API_READ_ACCESS_TOKEN"))
(def base-url "https://api.themoviedb.org/3")
(def base-headers
  {:Authorization (str "Bearer " api-read-access-token)})
(def base-params 
  {:headers base-headers
   :as :json-strict})

;; 
;; 
;; 
;; 
;; 
;; 
;; 

(def configuration-url (str base-url "/configuration"))
(def configuration-params {:headers base-headers
                           :as :json-strict})

(def cofiguration-cache-key :configuration)
(defn get-confguration! [] 
  (if-let [configuration (get @cache! cofiguration-cache-key)]
      configuration
    (let [response (client/get configuration-url configuration-params)
          configuration (-> response :body)]
      (swap! cache! assoc cofiguration-cache-key configuration)
      configuration)))
;; 
;; 
;; 
;; 
;; 
;; 
;; 
;; 

(defn assoc-image-urls [tmdb-data tmdb-configration]
  (let [base-url (-> tmdb-configration :images :secure_base_url)
        poster-size (-> tmdb-configration :images :poster_sizes last)
        backdrop-size  (-> tmdb-configration :images :backdrop_sizes last)]
    (-> tmdb-data
        (assoc :poster_url (str base-url poster-size (tmdb-data :poster_path)))
        (assoc :backdrop_url (str base-url backdrop-size (tmdb-data :backdrop_path))))))

(def tmdb-movie-keys->movie-keys
  {:id :movie/tmdb-id
   :title :movie/title
   :overview :movie/overview
   :release_date :movie/release-date
   :poster_path :movie/poster-path
   :poster_url :movie/poster-url
   :backdrop_path :movie/backdrop-path 
   :backdrop_url :movie/backdrop-url
   :vote_average :movie/vote-average
   :vote_count :movie/vote-count})

(defn youtube-embed-url [key]
  (str "https://www.youtube.com/embed/" key))

(defn youtube-watch-url [key]
  (str "https://www.youtube.com/watch?v=" key))

(defn assoc-youtube-video-url [video]
  (-> video
      (assoc :youtube-watch-url (youtube-watch-url (video :key)))
      (assoc :youtube-embed-url (youtube-embed-url (video :key)))))

(defn tmdb-video->video [tmdb-video]
  (rename-keys tmdb-video 
               {:id :video/tmdb-id
                :key :video/key
                :name :video/name
                :youtube-embed-url :video/youtube-embed-url
                :youtube-watch-url :video/youtube-watch-url}))

(defn tmdb->video [tmdb-video]
  (-> tmdb-video
      assoc-youtube-video-url
      tmdb-video->video))


(defn tmdb->movie [tmdb-configration tmdb-movie]
  (-> tmdb-movie
      (assoc-image-urls tmdb-configration)
      (rename-keys tmdb-movie-keys->movie-keys)
      (assoc :movie/id (:id tmdb-movie))))

(defn tmdb->movie! [tmdb-movie]
   (tmdb->movie (get-confguration!) tmdb-movie))

(defn tmdb->paginated-results [tmdb-paginated-results]
  (rename-keys tmdb-paginated-results 
               {:total_results :paginated/total-results
                :total_pages :paginated/total-pages
                :page :paginated/page
                :results :paginated/results}))

(defn map-paginated-results [paginated-result map-result]
  (let [results-new (map map-result (:paginated/results paginated-result))]
    (assoc paginated-result :paginated/results results-new)))

;; 
;; 
;; 
;; 
;; 
;; 


(defn movie-video-url [movie-id]
  (str base-url "/movie/" movie-id "/videos"))

(defn movie-videos-cache-key [movie-id]
  [:movie-videos movie-id])


(defn get-movie-videos! [movie-id]
  (if-let [videos (get @cache! (movie-videos-cache-key movie-id))]
      videos
    (let [response (client/get (movie-video-url movie-id) base-params)
          tmdb-videos (-> response :body :results)
          videos (map tmdb->video tmdb-videos)]
      (swap! cache! assoc (movie-videos-cache-key movie-id) videos)
      videos)))

(defn assoc-movie-videos! [movie] 
  (let [videos (get-movie-videos! (movie :movie/tmdb-id))]
      (assoc movie :movie/videos videos)))


;; 
;; 
;; 
;; 
;; 
;; 
;; 

(def discover-url (str base-url "/discover/movie"))
(def discover-query-params {:include_adult false
                            :include_video true
                            :language "en-US"
                            :page 1
                            :sort_by "popularity.desc"})
(def discover-params {:headers base-headers
                      :query-params discover-query-params
                      :as :json-strict})

(defn discover-cache-key [page]
  [:discover page])

(defn get-discover-from-source! []
  (let [response (client/get discover-url discover-params)
        results (-> response :body tmdb->paginated-results (map-paginated-results tmdb->movie!))]
    results))

(defn get-discover! []
  (if-let [discover (get @cache! (discover-cache-key 1))]
      discover
    (let [discover (get-discover-from-source!)]
      (swap! cache! assoc (discover-cache-key 1) discover)
      discover)))

(defn get-discover-with-videos! []
  (let [paginated-movies (get-discover!)
        paginated-movies-with-videos (map-paginated-results paginated-movies assoc-movie-videos!)]
    paginated-movies-with-videos))



;; 
;; 
;; 
;; 
;; 
;; 
;; 
;; 

(defn movie-details-url [movie-id]
  (str base-url "/movie/" movie-id))

(def movie-details-params 
  (merge-with merge base-params {:query-params {:language "en-US"}}))

(defn movie-details-cache-key [movie-id]
  [:movie-details movie-id])


(defn get-movie-details-from-source! [movie-id]
  (let [details-url (movie-details-url movie-id)
        details (client/get details-url movie-details-params)
        movie (-> details :body tmdb->movie!)
        movie-with-videos (assoc-movie-videos! movie)]
    movie-with-videos))

(defn get-movie-details! [movie-id]
  (if-let [movie (get @cache! (movie-details-cache-key movie-id))]
      movie
    (let [movie (get-movie-details-from-source! movie-id)]
      (swap! cache! assoc (movie-details-cache-key movie-id) movie)
      movie)))

;; 
;; 
;; 
;; 
;; 
;; 
;; 

(defrecord MoveDbTmdb [] 
  movie-db/MovieDb 
  (get! [_this movie-id]
    (get-movie-details! movie-id)) 
  (find! [_this _query]
    (get-discover-with-videos!)))

(defmethod movie-db/->MovieDb :movie-db-impl/tmdb
  [_]
  (->MoveDbTmdb))