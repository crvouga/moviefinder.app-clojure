(ns moviefinder-app.movie.movie-db-impl
  (:require [moviefinder-app.movie.movie-db-impl-tmdb]
            [moviefinder-app.movie.movie-db :as movie-db]))

(defn tmdb []
  (movie-db/->MovieDb
   {:movie-db/impl :movie-db-impl/tmdb}))
