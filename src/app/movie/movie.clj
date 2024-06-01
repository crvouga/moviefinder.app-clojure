(ns app.movie.movie)

(defn ->title [movie]
  (get movie :title))