(ns app.movie.details
  (:require [app.requests]
            [app.view]
            [app.route]
            [app.movie.db]
            [app.movie.db-impl :refer [movie-db]]))

(defn year [maybe-date-string]
  (when maybe-date-string
    (let [match (re-find #"(\d{4})" maybe-date-string)]
      (if match
        (Integer/parseInt (first match))
        nil))))

(defn view-movie-details! [request]
  (let [movie-id (-> request :request/route :movie/id)
        movie (app.movie.db/get! movie-db movie-id)]
    [:div.w-full.flex.flex-col.h-full.flex-1
     #_(-> movie pr-str)
     #_(app.view/top-bar {:top-bar/title (-> movie :movie/title)})
     [:div.w-full.flex.flex-col.h-full.flex-1.relative.pt-14.overflow-y-scroll.p-4.gap-6
      [:img.w-full.aspect-video.bg-neutral-900.absolute.top-0.left-0.-z-10.blur-sm {:src (-> movie :movie/backdrop-url)}]
      [:img.aspect-auto.bg-neutral-900.rounded.shadow-xl.mx-auto {:class "w-1/2" :src (-> movie :movie/poster-url)}]
      [:h1.font-bold.text-3xl.text-center (-> movie :movie/title)]
      [:h2.text-lg.text-center.text-neutral-300 (-> movie :movie/release-date year)]
      [:p.text-neutral-300.text-sm (-> movie :movie/overview)]]]))

(defmethod app.requests/route-hx :movie/detail [request]
  (app.requests/html (view-movie-details! request)))