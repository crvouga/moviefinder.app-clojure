(ns moviefinder-app.media.media-db-impl
  (:require [moviefinder-app.media.media-db :as media-db]
            [moviefinder-app.media.media-db-impl-combine :as media-db-impl-combine]
            [moviefinder-app.media.media-db-impl-in-memory :as media-db-impl-in-memory]
            [moviefinder-app.media.media-db-impl-postgres :as media-db-impl-postgres]
            [moviefinder-app.media.media-db-impl-tmdb-movie :as media-db-impl-tmdb-movie]
            [moviefinder-app.media.media-db-impl-tmdb-tv :as media-db-impl-tmdb-tv]))


(defn in-memory []
  (media-db-impl-in-memory/media-db-in-memory))

(defn postgres []
  (media-db-impl-postgres/media-db-postgres))

(defn tmdb-movie []
  (media-db-impl-tmdb-movie/media-db-tmdb-movie))

(defn tmdb-tv []
  (media-db-impl-tmdb-tv/media-db-tmdb-tv))

(defn media-db-combined-in-memory []
  (media-db-impl-combine/media-db-combine
   (in-memory)
   [(tmdb-movie) (tmdb-tv) (postgres)]))

(defn default []
  (media-db-combined-in-memory))

(def q
  {:q/where [[:q/>= :media/release-year 2010]
             [:q/= :media/genre :genre/horror]
             [:q/<= :media/release-year 2020]]
   :q/order [[:q/desc :media/popularity]
             [:q/asc :media/title]]})

(def media-db (default))


(comment
  (def media-db (default))
  (media-db/find! media-db q)
  ;;
  )