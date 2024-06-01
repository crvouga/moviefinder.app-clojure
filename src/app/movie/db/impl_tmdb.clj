(ns app.movie.db.impl-tmdb
  (:require [app.movie.db.core :refer [MovieDb]]))

(defrecord MoveDbTmdb [api-key]
  MovieDb
  (find-movies [_this query]
    (println "Finding movies for query" query)))