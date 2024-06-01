(ns app.movie.db.impl
  (:require [app.movie.db.impl-tmdb]))


(def movie-db (app.movie.db.impl-tmdb/->MoveDbTmdb))