(ns moviefinder-app.media.tmdb
  (:require [clojure.set :refer [rename-keys]]
            [moviefinder-app.env :as env]
            [moviefinder-app.http-client :as http-client]))

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

(def configuration-request 
  (assoc configuration-params
         :url configuration-url
         :method :get))

(defn get-configuration! []
  (-> (http-client/request-with-cache configuration-request)
      :body))
;; 
;; 
;; 
;; 
;; 
;; 

(defn assoc-image-urls [tmdb-data tmdb-configration]
  (let [base-url (-> tmdb-configration :images :secure_base_url)
        poster-size (-> tmdb-configration :images :poster_sizes (or []) last (or ""))
        backdrop-size  (-> tmdb-configration :images :backdrop_sizes (or []) last (or ""))]
    (-> tmdb-data
        (assoc :poster_url (str base-url poster-size (tmdb-data :poster_path)))
        (assoc :backdrop_url (str base-url backdrop-size (tmdb-data :backdrop_path))))))

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


(defn tmdb->paginated-results [tmdb-paginated-results]
  (rename-keys tmdb-paginated-results
               {:total_results :paginated/total-results
                :total_pages :paginated/total-pages
                :page :paginated/page
                :results :paginated/results}))