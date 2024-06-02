(ns app.home
  (:require [app.requests]
            [app.view]
            [app.route]
            [app.movie.db]
            [app.movie.db-impl :refer [movie-db]]))

(defn view-youtube-video [props]
  [:iframe.w-full.h-64 
   (merge props
          {:frameBorder "0" 
           :allow "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
           :allowFullScreen true})])


(defn view-feed-item [movie]
  [:div.w-full.flex.flex-col.justify-center.items-center.relative.h-full
   [:img.w-full.h-full.absolute.inset-0.-z-10.object-cover {:src (-> movie :movie/poster-url)}]
   #_[:div.w-full.flex-1.flex-col.justify-center.items-center.flex
    (let [youtube-video-url (-> movie :movie/videos first :video/youtube-embed-url)]
        (view-youtube-video {:src youtube-video-url}))] 
   [:div.w-full.p-4.pb-6
    [:a.text-2xl.font-bold.underline
     {:href (app.route/encode {:movie/id (-> movie :movie/id) 
                               :route/name :movie/detail})}
     (-> movie :movie/title)]]])

(defn view-swiper-slide [children]
  [:swiper-slide.w-full.h-full.overflow-hidden.max-h-full
   children])

;; https://swiperjs.com/element
(defn view-swiper-container [props children]
  [:swiper-container.w-full.flex-1.max-h-full.overflow-hidden (merge {:slides-per-view 1 :direction :vertical} props)
   children])

(defn view-feed [input]
  (let [movies (app.movie.db/find! movie-db {})]
    [:div.w-full.max-h-full.overflow-hidden.h-full.flex.flex-col
     (view-swiper-container {:initial-slide (-> input :request/route :feed/slide-index)}
      (for [movie (-> movies :paginated/results)]
        (view-swiper-slide
          (view-feed-item movie))))]))

(defn view-home [input]
  (app.view/view-app-tabs-layout {:route/name :home/home}  (view-feed input)))

(defmethod app.requests/route-hx :noop [_request]
  {:status 200})

(defmethod app.requests/route-hx :home/home [request]
  (app.requests/html (view-home request)))

