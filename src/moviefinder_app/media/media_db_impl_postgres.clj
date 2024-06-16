(ns moviefinder-app.media.media-db-impl-postgres
  (:require [moviefinder-app.media.media-db :as media-db]))


(defrecord MediaDbPostgres []
  media-db/MediaDb
  (get! [_this _media-id]
    nil)

  (find! [_this _query]
    {:paginated/page 1
     :paginated/total-pages 1
     :paginated/total-results 0
     :paginated/results []})

  (put-many! [_this _media-list]
    nil))

(defn media-db-postgres []
  (->MediaDbPostgres))