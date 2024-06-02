(ns moviefinder.movie.db)

(defprotocol MovieDb
  (find! [this query])
  (get! [this movie-id]))