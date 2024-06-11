(ns moviefinder-app.media.media-db)

(defprotocol MediaDb
  (get! [this id])
  (find! [this query]))

(defmulti ->MediaDb :media-db/impl)