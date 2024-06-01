(ns app.feed
  (:require [app.res]
            [app.view]
            [app.routes]
            [app.movie.movie]
            [app.movie.db.core]
            [app.movie.db.impl :refer [movie-db]]))

(def movies (app.movie.db.core/find-movies movie-db "star wars"))

(defn view-feed-panel []
  [:div [:h1 "Feed"]])

(defn view-feed-route []
  (app.routes/view-app-tabs-layout app.routes/route-feed (view-feed-panel)))

(defmethod app.res/req->res app.routes/route-feed [_]
  (app.res/html (view-feed-route)))

