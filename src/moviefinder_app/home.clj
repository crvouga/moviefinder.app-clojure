(ns moviefinder-app.home
  (:require [moviefinder-app.handle :as handle]
            [moviefinder-app.view :as view]
            [moviefinder-app.route :as route]
            [moviefinder-app.movie.movie-db :as movie-db]))

(defn view-youtube-video [props]
  [:iframe.w-full.h-64 
   (merge props
          {:frameBorder "0" 
           :allow "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
           :allowFullScreen true})])


(defn movie-details-href [movie]
  (route/encode 
   {:route/name :route/movie-details
    :movie/id (-> movie :movie/id)}))

(defn view-feed-item-title [movie]
  [:div.w-full.p-4.pb-6
   [:a.text-2xl.font-bold.underline
    {:href (movie-details-href movie)}
    (-> movie :movie/title)]])


(defn swiper-event-script []
  "
   document.addEventListener('DOMContentLoaded', function () {
     const swiperEl = document.querySelector('swiper-container');
     swiperEl.addEventListener('swiperslidechange', (event) => {
       const [swiper] = event.detail;
       console.log('slide changed', swiper.activeIndex);
     });
   });
  ")

(defn view-feed-slide [movie]
  [:div.w-full.flex.flex-col.justify-center.items-center.relative.h-full
   [:img.w-full.h-full.absolute.inset-0.-z-10.object-cover.bg-netural-200
    {:src (-> movie :movie/poster-url) :loading :lazy}]
   [:a.w-full.flex-1.flex-col.justify-center.items-center.flex
    {:hx-get (movie-details-href movie)
     :hx-target "#app"
     :href (movie-details-href movie)
     :hx-swap "innerHTML"
     :hx-push-url (movie-details-href movie)}]])

(defn view-feed-slides! [request]
  (let [movie-db (-> request :movie-db/movie-db)
        movies (movie-db/find! movie-db {})
        initial-slide (-> request :request/route :feed/slide-index)]
    (for [[slide-index movie] (map-indexed vector (->> movies :paginated/results))]
      [:swiper-slide.w-full.h-full.overflow-hidden.max-h-full
       {:id (str "slide-" slide-index)}
       [:div.w-full.h-full.flex-1.h-tmx-slide-content
        (view-feed-slide movie)]])))

(defmethod handle/handle-hx :route/changed-slide [_request]
  (handle/html [:div]))

(defn view-feed! [request]
  [:div#feed-container.w-full.max-h-full.overflow-hidden.h-full.flex.flex-col
   (view/view-raw-script (swiper-event-script))
   [:swiper-container#swiper-container.w-full.flex-1.max-h-full.overflow-hidden
    {:slides-per-view 1
     :direction :vertical
     :initial-slide (-> request :request/route :feed/slide-index)}
    (view-feed-slides! request)]])

(defn view-home [input]
  (view/app-tabs-layout {:route/name :route/home}  (view-feed! input)))

(defmethod handle/handle-hx :noop [_request]
  {:status 200})

(defmethod handle/handle-hx :route/home [request]
  (handle/html (view-home request)))

