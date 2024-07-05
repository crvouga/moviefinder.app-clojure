(ns moviefinder-app.media.media-db-impl-postgres
  (:require [moviefinder-app.media.media-db :as media-db]
            [moviefinder-app.paginated :as paginated]))


(defrecord MediaDbPostgres []
  media-db/MediaDb
  (get! [_this _media-id]
    nil)

  (find! [_this _query]
    (paginated/init))

  (put-many! [_this _media-list]
    nil))

(defn media-db-postgres []
  (->MediaDbPostgres))