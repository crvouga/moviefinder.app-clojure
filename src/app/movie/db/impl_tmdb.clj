(ns app.movie.db.impl-tmdb
  (:require
   [clj-http.client :as client]
   [clojure.set :refer [rename-keys]]
   [app.movie.db.core]
   [app.env]))

;; 
;; 
;; 
;; 
;; 
;; 
;; 

(def api-read-access-token (app.env/get-env-var! "TMDB_API_READ_ACCESS_TOKEN"))
(def base-url "https://api.themoviedb.org/3")
(def base-headers
  {:Authorization (str "Bearer " api-read-access-token)})
(def base-query-params 
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
(defn get-confguration! []
  (let [response (client/get configuration-url configuration-params)]
    (response :body)))

(def configuration (get-confguration!))

;; 
;; 
;; 
;; 
;; 
;; 
;; 
;; 

(defn assoc-image-urls [tmdb-data]
  (let [base-url (-> configuration :images :secure_base_url)
        poster-size (-> configuration :images :poster_sizes last)
        backdrop-size  (-> configuration :images :backdrop_sizes last)]
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

(defn youtube-video-url [key] (str "https://www.youtube.com/watch?v=" key))

(defn assoc-video-url [video]
  (-> video
      (assoc :url (youtube-video-url (video :key)))))

(def tmdb-video-keys->video-keys
  {:id :video/tmdb-id
   :key :video/key
   :name :video/name
   :url :video/url})

(defn tmdb->video [tmdb-video]
  (-> tmdb-video
      assoc-video-url
      (rename-keys tmdb-video-keys->video-keys)))


(defn tmdb->movie [tmdb-movie]
  (-> tmdb-movie
      assoc-image-urls
      (rename-keys tmdb-movie-keys->movie-keys)))

(defn tmdb->paginated-results [tmdb-paginated-results map-result]
{:total-results (:total_results tmdb-paginated-results)
 :total-pages (:total_pages tmdb-paginated-results)
 :page (:page tmdb-paginated-results)
 :results (map map-result (:results tmdb-paginated-results))})

(defn map-paginated-results [paginated-result map-result]
  (let [results-new (map map-result (:results paginated-result))]
    (assoc paginated-result :results results-new)))

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

(defn get-discover! []
  (let [response (client/get discover-url discover-params)
        results (tmdb->paginated-results (response :body) tmdb->movie)]
    (println "video" (-> response :body :results first :id))
    results))

;; 
;; 
;; 
;; 
;; 
;; 


(defn movie-video-url [movie-id]
  (str base-url "/movie/" movie-id "/videos"))

(def movie-videos-by-movie-id (atom {}))
(defn get-movie-videos! [movie-id]
  (if-let [videos (get @movie-videos-by-movie-id movie-id)]
      videos
    (let [response (client/get (movie-video-url movie-id) base-query-params)
          tmdb-videos (-> response :body :results)
          videos (map tmdb->video tmdb-videos)]
      (swap! movie-videos-by-movie-id assoc movie-id videos)
      videos)))

(defn assoc-movie-videos [movie] 
  (let [videos (get-movie-videos! (movie :movie/tmdb-id))]
      (assoc movie :movie/videos videos)))


;; 
;; 
;; 
;; 
;; 
;; 
;; 
;; 

(defrecord MoveDbTmdb [] 
  app.movie.db.core/MovieDb
  (find-movies [_this _query]
               (let [paginated-movies (get-discover!)
                     paginated-movies-with-videos (map-paginated-results paginated-movies assoc-movie-videos)]
                 paginated-movies-with-videos)))