(ns moviefinder-app.media.media-db-impl-tmdb-tv
  (:require [clj-http.client :as client]
            [clojure.set :refer [rename-keys]]
            [moviefinder-app.env :as env]
            [moviefinder-app.media.media-db :as media-db]
            [moviefinder-app.paginated :as paginated]))

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

(defn get-configuration-source! []
  (-> (client/get configuration-url configuration-params) :body))

(defn get-confguration! []
  (if-let [cached (get @cache! cofiguration-cache-key)]
    cached
    (let [source  (get-configuration-source!)]
      (swap! cache! assoc cofiguration-cache-key source)
      source)))
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

(def tmdb-tv-keys->media-keys
  {:id :media/tmdb-id
   :title :media/title
   :name :media/title
   :overview :media/overview
   :release_date :media/release-date
   :poster_path :media/poster-path
   :poster_url :media/poster-url
   :backdrop_path :media/backdrop-path
   :backdrop_url :media/backdrop-url
   :vote_average :media/vote-average
   :vote_count :media/vote-count})

(defn youtube-embed-url [key]
  (str "https://www.youtube.com/embed/" key))

(defn youtube-watch-url [key]
  (str "https://www.youtube.com/watch?v=" key))

(defn assoc-youtube-video-url [video]
  (-> video
      (assoc :youtube-watch-url (youtube-watch-url (video :key)))
      (assoc :youtube-embed-url (youtube-embed-url (video :key)))))

(def tmdb-video-keys->video-keys
  {:id :video/id
   :name :video/name
   :key :video/youtube-key
   :youtube-embed-url :video/youtube-embed-url
   :youtube-watch-url :video/youtube-watch-url})

(defn tmdb-video->video [tmdb-video]
  (-> tmdb-video
      (rename-keys tmdb-video-keys->video-keys)
      (select-keys (vals tmdb-video-keys->video-keys))))

(defn tmdb->video [tmdb-video]
  (-> tmdb-video
      assoc-youtube-video-url
      tmdb-video->video))


(defn tmdb-tv->media [tmdb-configration tmdb-tv]
  (-> tmdb-tv
      (assoc-image-urls tmdb-configration)
      (rename-keys tmdb-tv-keys->media-keys)
      (select-keys (vals tmdb-tv-keys->media-keys))
      (assoc :media/id (:id tmdb-tv))))

(defn tmdb->tv! [tmdb-tv]
  (tmdb-tv->media (get-confguration!) tmdb-tv))

(defn tmdb->paginated-results [tmdb-paginated-results]
  (rename-keys tmdb-paginated-results
               {:total_results :paginated/total-results
                :total_pages :paginated/total-pages
                :page :paginated/page
                :results :paginated/results}))

;; 
;; 
;; 
;; 
;; 
;; 


(defn tv-video-url [tv-id]
  (str base-url "/tv/" tv-id "/videos"))

(defn tv-videos-cache-key [tv-id]
  [:tv-videos tv-id])


(defn get-tv-videos-from-source! [tv-id]
  (let [response (client/get (tv-video-url tv-id) base-params)
        tmdb-videos (-> response :body :results)
        videos (map tmdb->video tmdb-videos)]
    videos))

(defn get-tv-videos! [tv-id]
  (if-let [cached (get @cache! (tv-videos-cache-key tv-id))]
    cached
    (let [source (get-tv-videos-from-source! tv-id)]
      (swap! cache! assoc (tv-videos-cache-key tv-id) source)
      source)))

(defn assoc-tv-videos! [tv]
  (let [videos (get-tv-videos! (tv :media/tmdb-id))]
    (assoc tv :media/videos videos)))


;; 
;; 
;; 
;; 
;; 
;; 
;; 

(def discover-url (str base-url "/discover/tv"))
(def discover-query-params {:include_adult "false"
                            :include_video "true"
                            :language "en-US"
                            :page 1
                            :sort_by "popularity.desc"})
(def discover-params {:headers base-headers
                      :query-params discover-query-params
                      :as :json-strict})

(defn discover-cache-key [page]
  [:discover page])

(defn get-discover-from-source! []
  (-> (client/get discover-url discover-params) 
      :body 
      tmdb->paginated-results
      (paginated/map-results tmdb->tv!)))

(defn get-discover! []
  (if-let [cached (get @cache! (discover-cache-key 1))]
    cached
    (let [source (get-discover-from-source!)]
      #_(swap! cache! assoc (discover-cache-key 1) source)
      source)))

(defn assoc-media-type [tv]
  (assoc tv :media/media-type :media-type/tv))

(defn get-discover-with-videos! []
  (-> (get-discover!) 
      (paginated/map-results  assoc-tv-videos!)
      (paginated/map-results  assoc-media-type)))



;; 
;; 
;; 
;; 
;; 
;; 
;; 
;; 

(defn tv-details-url [tv-id]
  (str base-url "/tv/" tv-id))

(def tv-details-params
  (merge-with merge base-params {:query-params {:language "en-US"}}))

(defn tv-details-cache-key [tv-id]
  [:tv-details tv-id])


(defn get-tv-details-from-source! [tv-id]
  (let [details-url (tv-details-url tv-id)
        details (client/get details-url tv-details-params)
        tv (-> details :body tmdb->tv!)
        tv-with-videos (assoc-tv-videos! tv)]
    tv-with-videos))

(defn get-tv-details! [tv-id]
  (if-let [cached (get @cache! (tv-details-cache-key tv-id))]
    cached
    (let [source (get-tv-details-from-source! tv-id)]
      (swap! cache! assoc (tv-details-cache-key tv-id) source)
      source)))

;; 
;; 
;; 
;; 
;; 
;; 
;; 

(defrecord MediaDbTmdbTv []
  media-db/MediaDb
  (get! [_this tv-id]
    (get-tv-details! tv-id))

  (find! [_this _query]
    (get-discover-with-videos!))

  (put-many! [_this _media-list]
    nil))

(defn media-db-tmdb-tv []
  (->MediaDbTmdbTv))


(comment
  (def media-db (media-db-tmdb-tv))

  (def q
    {:q/order [[:q/desc :media/popularity]
               [:q/asc :media/title]]
     :q/where [[:q/>= :media/release-year 2010]
               [:q/<= :media/release-year 2020]
               [:q/= :media/genre :genre/horror]]})

  (-> (media-db/find! media-db q) :paginated/results (nth 5)))