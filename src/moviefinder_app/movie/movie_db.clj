(ns moviefinder-app.movie.movie-db)

(defprotocol MovieDb
  (find! [this query])
  (get! [this movie-id]))

(defmulti ->MovieDb :movie-db/impl)