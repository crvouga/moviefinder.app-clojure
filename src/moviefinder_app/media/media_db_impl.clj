(ns moviefinder-app.media.media-db-impl
  (:require [moviefinder-app.media.media-db :as media-db]
            [moviefinder-app.media.media-db-impl-combine :as media-db-impl-combine]
            [moviefinder-app.media.media-db-impl-in-memory :as media-db-impl-in-memory]
            [moviefinder-app.media.media-db-impl-postgres :as media-db-impl-postgres]
            [moviefinder-app.media.media-db-impl-tmdb-movie :as media-db-impl-tmdb-movie]
            [moviefinder-app.media.media-db-impl-tmdb-tv :as media-db-impl-tmdb-tv]))



(defn media-db-combined-in-memory []
  (media-db-impl-combine/media-db-combine
   (media-db-impl-in-memory/media-db-in-memory)

   [(media-db-impl-postgres/media-db-postgres)
    (media-db-impl-tmdb-movie/media-db-tmdb-movie)
    (media-db-impl-tmdb-tv/media-db-tmdb-tv)]))

(comment 
  (def media-db (media-db-combined-in-memory))

  (def q 
    {:q/order [[:q/desc :media/popularity]
               [:q/asc :media/title]]
     :q/where [[:q/>= :media/release-year 2010]
               [:q/<= :media/release-year 2020]
               [:q/= :media/genre :genre/horror]]})
  
  (media-db/find! media-db q))