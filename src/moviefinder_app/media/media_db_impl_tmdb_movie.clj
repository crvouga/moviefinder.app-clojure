(ns moviefinder-app.media.media-db-impl-tmdb-movie
  (:require [clojure.set :refer [rename-keys]]
            [moviefinder-app.media.media-db :as media-db]
            [moviefinder-app.http-client :as http-client]
            [moviefinder-app.media.tmdb :as tmdb]
            [moviefinder-app.paginated :as paginated]))


(def tmdb-movie-keys->media-keys
  {:id :media/tmdb-id
   :title :media/title
   :overview :media/overview
   :release_date :media/release-date
   :poster_path :media/poster-path
   :popularity :media/popularity
   :poster_url :media/poster-url
   :backdrop_path :media/backdrop-path
   :backdrop_url :media/backdrop-url
   :vote_average :media/vote-average
   :vote_count :media/vote-count})

(defn tmdb->movie [tmdb-configration tmdb-movie]
  (-> tmdb-movie
      (tmdb/assoc-image-urls tmdb-configration)
      (rename-keys tmdb-movie-keys->media-keys)
      (select-keys (vals tmdb-movie-keys->media-keys))
      (assoc :media/id (:id tmdb-movie))))

(defn tmdb->movie! [tmdb-movie]
  (tmdb->movie (tmdb/get-configuration!) tmdb-movie))

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


(defn movie-video-url [movie-id]
  (str tmdb/base-url "/movie/" movie-id "/videos"))

(defn movie-video-request [movie-id]
  (-> tmdb/base-params
      (assoc :url (movie-video-url movie-id)
             :method :get)))

(defn get-movie-videos! [movie-id]
  (->> movie-id
       movie-video-request
       http-client/request-with-cache
       :body
       :results
       (map tmdb/tmdb->video)))

(defn assoc-movie-videos! [movie]
  (let [videos (get-movie-videos! (movie :media/tmdb-id))]
    (assoc movie :media/videos videos)))

(defn assoc-media-type [movie]
  (assoc movie :media/media-type :media-type/movie))


;; 
;; 
;; 
;; 
;; 
;; 
;; 

(def discover-url (str tmdb/base-url "/discover/movie"))
(def discover-query-params {:include_adult "false"
                            :include_video "true"
                            :language "en-US"
                            :page 1
                            :sort_by "popularity.desc"})
(def discover-params {:headers tmdb/base-headers
                      :query-params discover-query-params
                      :as :json-strict})

(def discover-request
  (-> discover-params
      (assoc :url discover-url
             :method :get)))

(defn get-discover! []
  (->> discover-request
       http-client/request-with-cache
       :body
       tmdb->paginated-results
       (paginated/map-results tmdb->movie!)))

(defn get-discover-with-videos! []
  (->> (get-discover!)
       (paginated/pmap-results assoc-movie-videos!)
       (paginated/map-results assoc-media-type)))

(get-discover-with-videos!)

;; 
;; 
;; 
;; 
;; 
;; 
;; 
;; 

(defn movie-details-url [movie-id]
  (str tmdb/base-url "/movie/" movie-id))

(def movie-details-params
  (merge-with merge tmdb/base-params {:query-params {:language "en-US"}}))

(defn get-movie-details-params [movie-id]
  (-> movie-details-params
      (assoc :url (movie-details-url movie-id)
             :method :get)))

(defn get-movie-details! [movie-id]
  (-> movie-id
      get-movie-details-params
      http-client/request-with-cache
      :body
      tmdb->movie!
      assoc-movie-videos!))

;; 
;; 
;; 
;; 
;; 
;; 
;; 

(defrecord MediaDbTmdbMovie []
  media-db/MediaDb
  (get! [_this movie-id]
    (get-movie-details! movie-id))

  (find! [_this _query]
    (get-discover-with-videos!))

  (put-many! [_this _media-list]
    nil))

(defn media-db-tmdb-movie []
  (->MediaDbTmdbMovie))


(def media-db (media-db-tmdb-movie))

(def q
  {:q/order [[:q/desc :media/popularity]
             [:q/asc :media/title]]
   :q/where [[:q/>= :media/release-year 2010]
             [:q/<= :media/release-year 2020]
             [:q/= :media/genre :genre/horror]]})
(comment
  (reset! tmdb/cache! {})
  (-> (media-db/find! media-db q) :paginated/results (nth 5)))