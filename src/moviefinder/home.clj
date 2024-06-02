(ns moviefinder.home
  (:require [moviefinder.requests]
            [moviefinder.view]
            [moviefinder.route]
            [moviefinder.movie.db]
            [moviefinder.movie.db-impl :refer [movie-db]]))

(defn view-youtube-video [props]
  [:iframe.w-full.h-64 
   (merge props
          {:frameBorder "0" 
           :allow "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
           :allowFullScreen true})])


(defn movie-details-href [movie]
  (moviefinder.route/encode {:movie/id (-> movie :movie/id)
                     :route/name :movie/detail}))

(defn view-feed-item-title [movie]
  [:div.w-full.p-4.pb-6
   [:a.text-2xl.font-bold.underline
    {:href (movie-details-href movie)}
    (-> movie :movie/title)]])

(defn view-feed-item [movie]
  [:div.w-full.flex.flex-col.justify-center.items-center.relative.h-full
   [:img.w-full.h-full.absolute.inset-0.-z-10.object-cover {:src (-> movie :movie/poster-url)}]
   [:a.w-full.flex-1.flex-col.justify-center.items-center.flex {:href (movie-details-href movie)}
    #_(let [youtube-video-url (-> movie :movie/videos first :video/youtube-embed-url)]
        (view-youtube-video {:src youtube-video-url}))] 
   #_(view-feed-item-title movie)])

(defn view-swiper-slide [children]
  [:swiper-slide.w-full.h-full.overflow-hidden.max-h-full
   children])

;; https://swiperjs.com/element
(defn view-swiper-container [props children]
  [:swiper-container.w-full.flex-1.max-h-full.overflow-hidden (merge {:slides-per-view 1 :direction :vertical} props)
   children])

(defn view-feed [input]
  (let [movies (moviefinder.movie.db/find! movie-db {})]
    [:div.w-full.max-h-full.overflow-hidden.h-full.flex.flex-col
     (view-swiper-container {:initial-slide (-> input :request/route :feed/slide-index)}
      (for [movie (-> movies :paginated/results)]
        (view-swiper-slide
          (view-feed-item movie))))]))

(defn view-home [input]
  (moviefinder.view/view-app-tabs-layout {:route/name :home/home}  (view-feed input)))

(defmethod moviefinder.requests/route-hx :noop [_request]
  {:status 200})

(defmethod moviefinder.requests/route-hx :home/home [request]
  (moviefinder.requests/html (view-home request)))

