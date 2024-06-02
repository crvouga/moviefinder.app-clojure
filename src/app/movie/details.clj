(ns app.movie.details
  (:require [app.requests]
            [app.view]
            [app.route]
            [app.movie.db]
            [app.movie.db-impl :refer [movie-db]]))

(defn view-movie-details! [request]
  (let [movie-id (-> request :request/route :movie/id)
        movie (app.movie.db/get! movie-db movie-id)]
    [:div.w-full.flex.flex-col.h-full.flex-1
     (app.view/top-bar {:top-bar/title (-> movie :movie/title)})
     "hello"]))

(defmethod app.requests/route-hx :movie/detail [request]
  (app.requests/html (view-movie-details! request)))