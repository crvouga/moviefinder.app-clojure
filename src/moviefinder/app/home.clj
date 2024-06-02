(ns moviefinder.app.home
  (:require [moviefinder.app.requests]
            [moviefinder.app.view]
            [moviefinder.app.route]
            [moviefinder.app.movie.db]
            [moviefinder.app.movie.db-impl :refer [movie-db]]))

(defn view-youtube-video [props]
  [:iframe.w-full.h-64 
   (merge props
          {:frameBorder "0" 
           :allow "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
           :allowFullScreen true})])


(defn movie-details-href [movie]
  (moviefinder.app.route/encode 
   {:route/name :movie/detail
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
  (let [movies (moviefinder.app.movie.db/find! movie-db {})]
    (for [[slide-index movie] (map-indexed vector (-> movies :paginated/results))]
      [:swiper-slide.w-full.h-full.overflow-hidden.max-h-full
       {:hx-post (moviefinder.app.route/encode {:route/name :noop})
        :hx-target "none"
        :hx-trigger "htmx:swiperSlideChange"
        :hx-push-url (-> input :request/route (assoc :feed/slide-index slide-index) moviefinder.app.route/encode)}
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

(def view-swiper-event-script
  (moviefinder.app.view/view-raw-script swiper-event-script))

(defn view-feed! [input]
  [:div.w-full.max-h-full.overflow-hidden.h-full.flex.flex-col
   view-swiper-event-script
   [:swiper-container.w-full.flex-1.max-h-full.overflow-hidden
    {:slides-per-view 1
     :direction :vertical
     :initial-slide (-> input :request/route :feed/slide-index)}
    (view-feed-slides! input)]])

(defn view-home [input]
  (moviefinder.app.view/view-app-tabs-layout {:route/name :home/home}  (view-feed! input)))

(defmethod moviefinder.app.requests/route-hx :noop [_request]
  {:status 200})

(defmethod moviefinder.app.requests/route-hx :home/home [request]
  (moviefinder.app.requests/html (view-home request)))

