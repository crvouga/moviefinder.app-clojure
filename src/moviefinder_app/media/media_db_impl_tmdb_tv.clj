(ns moviefinder-app.media.media-db-impl-tmdb-tv
  (:require [clojure.set :refer [rename-keys]]
            [moviefinder-app.media.media-db :as media-db]
            [moviefinder-app.media.tmdb :as tmdb]
            [moviefinder-app.paginated :as paginated]
            [moviefinder-app.http-client :as http-client]))

(def tmdb-tv-keys->media-keys
  {:id :media/tmdb-id
   :title :media/title
   :name :media/title
   :overview :media/overview
   :release_date :media/release-date
   :poster_path :media/poster-path
   :popularity :media/popularity
   :poster_url :media/poster-url
   :backdrop_path :media/backdrop-path
   :backdrop_url :media/backdrop-url
   :vote_average :media/vote-average
   :vote_count :media/vote-count})


(defn tmdb-tv->media [tmdb-configration tmdb-tv]
  (-> tmdb-tv
      (tmdb/assoc-image-urls tmdb-configration)
      (rename-keys tmdb-tv-keys->media-keys)
      (select-keys (vals tmdb-tv-keys->media-keys))
      (assoc :media/id (:id tmdb-tv))))

(defn tmdb->tv! [tmdb-tv]
  (tmdb-tv->media (tmdb/get-configuration!) tmdb-tv))



;; 
;; 
;; 
;; 
;; 
;; 


(defn tv-video-url [tv-id]
  (str tmdb/base-url "/tv/" tv-id "/videos"))

(defn tv-videos-request [tv-id]
  (-> tmdb/base-params
      (assoc :url (tv-video-url tv-id)
             :method :get)))

(defn get-tv-videos! [tv-id]
  (-> tv-id
      tv-videos-request
      http-client/request
      :body
      :results))

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

(def discover-url (str tmdb/base-url "/discover/tv"))
(def discover-query-params {:include_adult "false"
                            :include_video "true"
                            :language "en-US"
                            :page 1
                            :sort_by "popularity.desc"})
(def discover-params {:headers tmdb/base-headers
                      :query-params discover-query-params
                      :as :json-strict})

(defn discover-request []
  (-> discover-params
      (assoc :url discover-url
             :method :get)))

(defn get-discover! []
  (->> (discover-request)
       http-client/request-with-cache
       :body
       tmdb/tmdb->paginated-results
       (paginated/map-results tmdb->tv!)))

(defn assoc-media-type [tv]
  (assoc tv :media/media-type :media-type/tv))

(defn- find! [_query]
  (->> (get-discover!)
       #_(paginated/pmap-results assoc-tv-videos!)
       (paginated/map-results assoc-media-type)))
(comment
  (reset! tmdb/cache! {})
  (find! {})
  
  )

;; 
;; 
;; 
;; 
;; 
;; 
;; 
;; 

(defn tv-details-url [tv-id]
  (str tmdb/base-url "/tv/" tv-id))

(def tv-details-params
  (merge-with merge tmdb/base-params {:query-params {:language "en-US"}}))

(defn tv-details-request [tv-id]
  (-> tv-id
      tv-details-params
      (assoc :url (tv-details-url tv-id)
             :method :get)))

(defn get-tv-details! [tv-id]
  (-> tv-id
      tv-details-request
      http-client/request-with-cache
      :body
      tmdb->tv!
      assoc-tv-videos!))

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

  (find! [_this query]
    (find! query))

  (put-many! [_this _media-list]
    nil))

(defn media-db-tmdb-tv []
  (->MediaDbTmdbTv))

(def media-db (media-db-tmdb-tv))

(def q
  {:q/order [[:q/desc :media/popularity]
             [:q/asc :media/title]]
   :q/where [[:q/>= :media/release-year 2010]
             [:q/<= :media/release-year 2020]
             [:q/= :media/genre :genre/horror]]})
(comment 
  (-> (media-db/find! media-db q) :paginated/results (rand-nth))
  
  )