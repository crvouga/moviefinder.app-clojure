(ns moviefinder-app.home
  (:require [moviefinder-app.handle :as handle]
            [moviefinder-app.media-feedback.media-feedback :as media-feedback]
            [moviefinder-app.media.media-db :as media-db]
            [moviefinder-app.route :as route]
            [moviefinder-app.view :as view]
            [moviefinder-app.view.icon :as icon]))

(defn view-youtube-video [props]
  [:iframe.w-full.h-64 
   (merge props
          {:frameBorder "0" 
           :allow "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
           :allowFullScreen true})])


(defn media-details-href [media]
  (route/encode 
   {:route/name :route/media-details
    :media/id (-> media :media/id)}))

(defn view-feed-slide [input]
  [:div.w-full.flex.flex-col.justify-center.items-center.relative.h-full
   [:div.w-full.flex.flex-col.justify-center.items-center.relative.flex-1
    {:hx-boost true}
    [:img.w-full.h-full.absolute.inset-0.-z-10.object-cover.bg-netural-200.min-h-full.min-w-full
     {:src (-> input ::media :media/poster-url)}]
    [:a.w-full.flex-1.flex-col.justify-center.items-center.flex
     {:hx-get (-> input ::media media-details-href)
      :hx-boost true
      :href (-> input ::media media-details-href)
      :hx-swap "innerHTML"
      :hx-target "#app"
      :hx-push-url (-> input ::media media-details-href)}]]
   #_(media-feedback/view-media-feedback-form)])

(defn slide-id [slide-index]
  (str "slide-" slide-index))

(defn view-feed-slides! [request]
  (let [media-db (-> request :media-db/media-db)
        media (media-db/find! media-db {})]
    (for [[slide-index media] (map-indexed vector (->> media :paginated/results))]
      [:swiper-slide.w-full.h-full.overflow-hidden.max-h-full
       {:id (slide-id slide-index)}
       [:div.w-full.h-full.flex-1
        {:hx-trigger (str "slide-changed from:#" (slide-id slide-index))
         :hx-target "this"
         :hx-swap "none"
         :hx-get (-> {:route-name :route-changed-slide :feed/slide-index slide-index} route/encode)
         :hx-push-url (-> request :request/route (assoc :feed/slide-index slide-index) route/encode)}
        (view-feed-slide (assoc request ::media media))]])))

(defmethod handle/hx-get :route/changed-slide [request]
  (-> request
      (handle/html (fn [] [:div]))))


(defn view-filter-chips []
  [:div.w-full.flex.items-center.p-4.gap-3
   (view/chip {:chip/label "Trending"
               :chip/variant :chip/contained})
   (view/chip {:chip/label "Movie"
               :chip/variant :chip/contained})
   (view/chip {:chip/label "TV"
               :chip/variant :chip/contained})])


(defn view-feed! [request]
  [:div#feed-container.w-full.max-h-full.overflow-hidden.h-full.flex.flex-col
   [:div.w-full.flex.items-center
    [:div.flex-1 (view-filter-chips)]
    [:button.size-16.flex.items-center.justify-center
     (icon/adjustments-horizontal)]]
   [:swiper-container#swiper-container.w-full.flex-1.max-h-full.overflow-hidden
    {:slides-per-view 1
     :direction :vertical
     :initial-slide (-> request :request/route :feed/slide-index)}
    (view-feed-slides! request)]])

(defn view-home [input]
  (view/app-tabs-layout {:route/name :route/home}  (view-feed! input)))

(defmethod handle/hx-get :route/home [request]
  (-> request
      (handle/html view-home)))

