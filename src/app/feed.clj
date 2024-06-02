(ns app.feed
  (:require [app.res]
            [app.view]
            [app.movie.movie]
            [app.movie.db.core]
            [app.movie.db.impl :refer [movie-db]]))

(defn view-youtube-video [props]
  [:iframe.w-full.h-64 
   (merge props
          {:frameBorder "0" 
           :allow "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
           :allowFullScreen true})])


(defn view-feed-item [movie]
  [:div.w-full.flex.flex-col.justify-center.items-center.relative.h-full
   [:img.w-full.h-full.absolute.inset-0.-z-10.object-cover {:src (-> movie :movie/poster-url)}]
   [:div.w-full.flex-1.flex-col.justify-center.items-center.flex
    #_(let [youtube-video-url (-> movie :movie/videos first :video/:youtube-video-url)]
      (view-youtube-video {:src youtube-video-url}))] 
   [:div.w-full.p-4.pb-6
    [:p.text-2xl.font-bold (-> movie :movie/title)]]])

(defn view-swiper-slide [children]
  [:swiper-slide.w-full.h-full.overflow-hidden.max-h-full
   children])

(defn view-swiper-container [children]
  [:swiper-container.w-full.flex-1.max-h-full.overflow-hidden {:slides-per-view 1 :direction :vertical}
   children])

(defn view-swiper [view-slides]
  (view-swiper-container
   (for [vide-slide view-slides]
     (view-swiper-slide vide-slide))))

(defn view-feed-panel [{:keys [movie-db]}]
  (let [movies (app.movie.db.core/find-movies! movie-db {})]
    [:div.w-full.max-h-full.overflow-hidden.h-full.flex.flex-col
     (view-swiper
      (for [movie (-> movies :results)]
         (view-feed-item movie)))]))

(defn view-feed-route [input]
  (app.view/view-app-tabs-layout {:route/name :feed/index}  (view-feed-panel input)))

(defmethod app.res/handle :feed/index [_]
  (app.res/html (view-feed-route {:movie-db movie-db})))

