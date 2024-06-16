(ns moviefinder-app.home
  (:require [moviefinder-app.handle :as handle]
            [moviefinder-app.media-feedback.media-feedback :as media-feedback]
            [moviefinder-app.media.media-db :as media-db]
            [moviefinder-app.route :as route]
            [moviefinder-app.view :as view]))

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
      :hx-push-url (-> input ::media media-details-href)}]]
   (media-feedback/view-media-feedback-form)])

(defn swiper-event-script []
  "
   function initializeSwiperEventScript() {
     const swiperEl = document.querySelector('swiper-container')
     
     if (!swiperEl) {
       return;
     }
      
     swiperEl.addEventListener('swiperslidechange', (event) => {
       const [swiper] = event.detail;
       const slideId = `slide-${swiper.activeIndex}`;
       const slideEl = document.getElementById(slideId);
       if (slideEl) {
         const event = new CustomEvent('slide-changed');
         slideEl.dispatchEvent(event);
       }
     });
   
     window.addEventListener('popstate', (event) => {
       const base64 = window.location.pathname.split('/')[1]
       const routeEdn = atob(base64);
       console.log(routeEdn);
       // swiperEl.swiper.slideTo(slideIndex);
     });
   }

   initializeSwiperEventScript()
   document.addEventListener('DOMContentLoaded', initializeSwiperEventScript);  
   if(typeof observer === 'undefined') {
    const observer = new MutationObserver((mutationsList, observer) => {
      for (const mutation of mutationsList) {
        if (mutation.type === 'childList') {
          mutation.addedNodes.forEach(node => {
            if (node.id === 'feed-container') {
              console.log('#feed-container added');
              initializeSwiperEventScript();
            }
          });
          mutation.removedNodes.forEach(node => {
            if (node.id === 'feed-container') {
              console.log('#feed-container removed');
            }
          });
        }
      }
    });
    observer.observe(document.body, { childList: true, subtree: true });
   }
  ")

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

(defmethod handle/hx-get :route/home [request]
  (-> request
      (handle/html view-home)))

