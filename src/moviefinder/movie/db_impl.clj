(ns moviefinder.movie.db-impl
  (:require [moviefinder.movie.db-impl-tmdb]))


(def movie-db (moviefinder.movie.db-impl-tmdb/->MoveDbTmdb))