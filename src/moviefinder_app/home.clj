(ns moviefinder-app.home
  (:require [moviefinder-app.requests :as requests]
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

(defn view-feed-slide [movie]
  [:div.w-full.flex.flex-col.justify-center.items-center.relative.h-full
   [:img.w-full.h-full.absolute.inset-0.-z-10.object-cover.bg-netural-200 {:src (-> movie :movie/poster-url) :loading :lazy}]
   [:a.w-full.flex-1.flex-col.justify-center.items-center.flex
    {:hx-get (movie-details-href movie)
     :hx-target "#app"
     :href (movie-details-href movie)
     :hx-swap "innerHTML"
     :hx-push-url (movie-details-href movie)}

    #_(let [youtube-video-url (-> movie :movie/videos first :video/youtube-embed-url)]
        (view-youtube-video {:src youtube-video-url}))]
   #_(view-feed-item-title movie)])

;; https://swiperjs.com/element

(defn view-feed-slides! [input]
  (let [movie-db (-> input :movie-db/movie-db)
        movies (movie-db/find! movie-db {})]
    (for [[slide-index movie] (map-indexed vector (-> movies :paginated/results))]
      [:swiper-slide.w-full.h-full.overflow-hidden.max-h-full
       {:hx-post (route/encode {:route/name :noop})
        :hx-target "none"
        :hx-trigger "htmx:swiperSlideChange"
        :hx-push-url (-> input :request/route (assoc :feed/slide-index slide-index) route/encode)}
       (view-feed-slide movie)])))

(def swiper-event-script
   "document.addEventListener('DOMContentLoaded', function () {
           const swiper = new Swiper('.swiper-container', {
              pagination: {
                el: '.swiper-pagination',
              },
              navigation: {
                nextEl: '.swiper-button-next',
                prevEl: '.swiper-button-prev',
              },
            });
    
            swiper.on('slideChange', function () {
              const event = new CustomEvent('htmx:swiperSlideChange', {
                detail: { index: swiper.activeIndex },
              });
              console.log('slideChange', swiper.activeIndex)
              window.dispatchEvent(event);
            });
          });")

(defn view-feed! [input]
  [:div.w-full.max-h-full.overflow-hidden.h-full.flex.flex-col
   (view/view-raw-script swiper-event-script)
   [:swiper-container.w-full.flex-1.max-h-full.overflow-hidden
    {:slides-per-view 1
     :direction :vertical
     :initial-slide (-> input :request/route :feed/slide-index)}
    (view-feed-slides! input)]])

(defn view-home [input]
  (view/view-app-tabs-layout {:route/name :route/home}  (view-feed! input)))

(defmethod requests/handle-hx :noop [_request]
  {:status 200})

(defmethod requests/handle-hx :route/home [request]
  (requests/html (view-home request)))

