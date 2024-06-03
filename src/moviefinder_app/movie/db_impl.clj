(ns moviefinder-app.movie.db-impl
  (:require [moviefinder-app.movie.db-impl-tmdb]))


(def movie-db (moviefinder-app.movie.db-impl-tmdb/->MoveDbTmdb))