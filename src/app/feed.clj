(ns app.feed
  (:require [app.res]
            [app.view]
            [app.routes]
            [app.movie.movie]
            [app.movie.db.core]
            [app.movie.db.impl :refer [movie-db]]))


(defn view-feed-item [movie]
  [:div.w-full.flex.flex-col.justify-center.items-center
   [:img.w-full.h-full {:src (-> movie :movie/poster-url)}]
   #_(let [youtube-video-url (-> movie :movie/videos first :video/:youtube-video-url)]
     [:iframe.w-full.h-64 {:src youtube-video-url
                           :frameBorder "0"
                           :allow "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                           :allowFullScreen true}])
   [:p (-> movie :movie/title)]])

(defn view-feed-panel []
  (let [movies (app.movie.db.core/find-movies movie-db {})]
    [:div
     [:h1 "Feed"]
     [:pre (pr-str movies)]
     [:swiper-container.flex.flex-col.w-full.h-full.max-h-full {:slides-per-view 1 :direction :vertical}
      (for [movie (-> movies :results)]
        [:swiper-slide.w-full.h-full.overflow-hidden.max-h-full
         (view-feed-item movie)])]]))

(defn view-feed-route []
  (app.routes/view-app-tabs-layout app.routes/route-feed (view-feed-panel)))

(defmethod app.res/req->res app.routes/route-feed [_]
  (app.res/html (view-feed-route)))

