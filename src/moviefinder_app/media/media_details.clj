(ns moviefinder-app.media.media-details
  (:require [moviefinder-app.handle :as handle]
            [moviefinder-app.view :as view]
            [moviefinder-app.media.media-db :as media-db]
            [moviefinder-app.media-feedback.media-feedback :as media-feedback]
            [moviefinder-app.view.icon :as icon]
            [moviefinder-app.route :as route]))

(defn year [maybe-date-string]
  (when maybe-date-string
    (let [match (re-find #"(\d{4})" maybe-date-string)]
      (if match
        (Integer/parseInt (first match))
        nil))))

(defn view-backdrop [media]
  [:img.w-full.aspect-video.bg-neutral-900 {:src (-> media :media/backdrop-url)}])

(defn view-title [media]
  [:h1.font-bold.text-3xl.text-center (-> media :media/title)])

(defn view-year [media]
  [:h2.text-lg.text-center.text-neutral-300 (-> media :media/release-date year)])

(defn view-overview [media]
  [:p.text-neutral-300.text-sm.text-center (-> media :media/overview)])

(def view-gutter [:div.w-full.p-8])

(defn view-section-title [title]
  [:h2.text-xl.text-left.font-bold title])

(defn ->youtube-thumbnail-url [youtube-id]
  (str "https://img.youtube.com/vi/" youtube-id "/hqdefault.jpg"))

(defn x-video-youtube-key [video]
  (str "\"" (-> video :video/youtube-key) "\""))

(defn x-video-visible? [video]
  (str "videoYoutubeKey === " (x-video-youtube-key video)))

(defn x-toggle-video [video]
  (str "videoYoutubeKey = " (x-video-visible? video) " ? null : " (x-video-youtube-key video)))

(defn view-youtube-thumbnail [youtube-id]
  [:img.aspect-video.bg-neutral-900.rounded.shadow-xl.w-36.object-cover
   {:src (->youtube-thumbnail-url youtube-id)}])

(defn view-video-item [video]
  [:button.text-sm.text-neutral-300.flex.items-center.w-full.gap-2
   {:x-on:click (x-toggle-video video)
    :target "_blank"
    :rel "noopener noreferrer"}
   (view-youtube-thumbnail (-> video :video/youtube-key))
   [:p.flex-1.text-left
    (-> video :video/name)]
   [:div.px-4 {:x-show (x-video-visible? video)}
    (icon/checkmark)]])

(defn view-video-section [media]
  [:div.flex.flex-col.gap-4
   [:div.px-4
    (view-section-title "Videos")]
   
   [:ul.flex.flex-col.divide-y.divide-neutral-800
    (for [video (-> media :media/videos)]
      [:li
       (view-video-item video)])]])

(defn x-ref-video-iframe-id [video]
  (str "iframe-" (-> video :video/youtube-key)))

(def js-pause-iframe
  "iframe.contentWindow.postMessage('{\"event\":\"command\",\"func\":\"pauseVideo\",\"args\":\"\"}', '*');")

(def js-play-iframe
  "iframe.contentWindow.postMessage('{\"event\":\"command\",\"func\":\"playVideo\",\"args\":\"\"}', '*');")

(defn js-should-pause-iframe [video]
  (str "iframe && videoYoutubeKey !== '" (-> video :video/youtube-key) "'"))

(defn js-const-iframe [video]
  (let [ref-id (x-ref-video-iframe-id video)
        ref (str "$refs[\"" ref-id "\"]")]
    (str "const iframe = " ref ";")))

(defn js-if [condition then else]
  (str "if (" condition ") {\n"
       "\t" then "\n"
       "} else {\n"
       "\t" else "\n"
       "};\n"))

(defn x-effect-iframe-pause-effect [video] 
  (str (js-const-iframe video)
       (js-if (js-should-pause-iframe video) 
              js-pause-iframe 
              js-play-iframe)))

(defn view-embedded-video [video]
  [:iframe.w-full.h-full
   {:src (str "https://www.youtube.com/embed/" (-> video :video/youtube-key) "?enablejsapi=1")
    :x-ref (-> video x-ref-video-iframe-id)
    :x-effect (-> video x-effect-iframe-pause-effect)
    :frameborder "0"
    :allow "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
    :allowfullscreen true}])

(defmethod handle/hx-get :route/video [request]
  (-> request
      (merge (-> request :request/route))
      (handle/html view-embedded-video)))


(defn video-route [video]
  (-> {:route/name :route/video} (merge video) route/encode))

(defn view-load-video [video]
  [:div.w-full.h-full.flex.items-center.justify-center.bg-neutral-800.animate-pulse
   {:hx-trigger "intersect"
    :hx-target "this"
    :hx-swap "outerHTML"
    :data-loading-path (-> video video-route)
    :hx-get (-> video video-route)}
   (view/spinner)])

(defn view-video-players [media]
  (for [video (->> media :media/videos)]
    [:div.absolute.top-0.left-0.w-full
     {:x-show (x-video-visible? video)}
     [:div.aspect-video.w-full.bg-neutral-900
      (view-load-video video)]
     (view/button {:button/label "Close"
                   :x-on:click "videoYoutubeKey = -1"
                   :button/size :button/sm})]))

(defn view-details [media]
  [:div.w-full.flex.flex-col.flex-1.overflow-hidden.relative
   {:x-data "{ videoPlayerOpen: false, videoYoutubeKey: null }"}
   
   (moviefinder-app.view/top-bar {:top-bar/title (-> media :media/title)})
   
   (view-video-players media)
   
   [:div.w-full.flex.flex-col.flex-1.overflow-y-scroll.gap-4
    (view-backdrop media)
    [:div.flex.w-full.justify-center.gap-4
     [:div.flex.flex-col
      (view-title media)
      (view-year media)]]

    [:div.px-4.flex.flex-col.gap-4
     (view-overview media)]
    (view-video-section media)

    view-gutter]
   
   (media-feedback/view-media-feedback-form media)])

(defn view-details! [request]
  (let [media-id (-> request :request/route :media/id)
        media-db (-> request :media-db/media-db)
        media (media-db/get! media-db media-id)]
    (view/app-tabs-layout :route/home (view-details media))))

(defmethod moviefinder-app.handle/hx-get :route/media-details [request]
  (-> request
      (handle/html view-details!)))