(ns app.movie.db.core)

(defprotocol MovieDb 
  (find-movies! [this query]))

