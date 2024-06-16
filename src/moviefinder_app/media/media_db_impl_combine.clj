(ns moviefinder-app.media.media-db-impl-combine 
  (:require [moviefinder-app.media.media-db :as media-db]))

(defrecord MediaDbCombine [media-db media-dbs]
  moviefinder-app.media.media-db/MediaDb
  (get! [_this media-id]
    (or (media-db/get! media-db media-id)
        (some->> media-dbs
                 (map #(media-db/get! % media-id))
                 (some identity))))
  (find! [_this query]
    (let [results (mapcat #(media-db/find! % query) media-dbs)]
      {:paginated/page 1
       :paginated/total-pages 1
       :paginated/total-results (count results)
       :paginated/results results}))
  
  (put-many! [_this media-list]
    (doseq [media-db media-dbs]
      (media-db/put-many! media-db media-list))))


(defn media-db-combine [media-db media-dbs]
  (->MediaDbCombine  media-db media-dbs))