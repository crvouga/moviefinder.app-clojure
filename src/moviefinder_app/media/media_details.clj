(ns moviefinder-app.media.media-details
  (:require [moviefinder-app.handle :as handle]
            [moviefinder-app.view :as view]
            [moviefinder-app.media.media-db :as media-db]
            [moviefinder-app.media-feedback.media-feedback :as media-feedback]
            [moviefinder-app.view.icon :as icon]))

(defn year [maybe-date-string]
  (when maybe-date-string
    (let [match (re-find #"(\d{4})" maybe-date-string)]
      (if match
        (Integer/parseInt (first match))
        nil))))

(defn view-backdrop [media]
  [:img.w-full.aspect-video.bg-neutral-900 {:src (-> media :media/backdrop-url)}])

(defn view-poster [media]
  [:img.aspect-auto.bg-neutral-900.rounded.shadow-xl {:class "w-1/5" :src (-> media :media/poster-url)}])

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

(defn video-visible? [video]
  (str "videoYoutubeKey === " "\"" (-> video :video/youtube-key) "\""))

(defn view-youtube-thumbnail [youtube-id]
  [:img.aspect-video.bg-neutral-900.rounded.shadow-xl.w-36.object-cover
   {:src (->youtube-thumbnail-url youtube-id)}])

(defn view-video-item [video]
  [:button.text-sm.text-neutral-300.flex.items-center.w-full.gap-2
   {;; :href (-> video :video/youtube-watch-url)
    :x-on:click (str  "videoYoutubeKey = " "\"" (-> video :video/youtube-key) "\"")
    :target "_blank"
    :rel "noopener noreferrer"}
   (view-youtube-thumbnail (-> video :video/youtube-key))
   [:p.flex-1.text-left
    (-> video :video/name)]
   [:div.px-4 {:x-show (video-visible? video)}
    (icon/checkmark)]])

(defn view-videos [media]
  [:div.flex.flex-col.gap-4
   [:div.px-4
    (view-section-title "Videos")]
   
   [:ul.flex.flex-col.divide-y.divide-neutral-800
    (for [video (-> media :media/videos)]
      [:li
       (view-video-item video)])]])

(defn view-embedded-video [video]
  [:iframe.w-full.h-full
   {:src (str "https://www.youtube.com/embed/" (-> video :video/youtube-key))
    :frameborder "0"
    :allow "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
    :allowfullscreen true}])

(defn view-details [media]
  [:div.w-full.flex.flex-col.flex-1.overflow-hidden.relative
   {:x-data "{ videoPlayerOpen: false, videoYoutubeKey: null }"}
   
   #_(moviefinder-app.view/top-bar {:top-bar/title (-> media :media/title)})
   
   (for [video (->> media :media/videos)]
     [:div.absolute.top-0.left-0.w-full
      {:x-show (video-visible? video)}
      [:div.aspect-video.w-full.bg-neutral-900
       (view-embedded-video video)]
      (view/button {:button/label "Close"
                    :x-on:click "videoYoutubeKey = -1"
                    :button/size :button/sm})])
   
   [:div.w-full.flex.flex-col.flex-1.overflow-y-scroll.gap-4
    (view-backdrop media)
    [:div.flex.w-full.justify-center.gap-4
     #_(view-poster media)

     [:div.flex.flex-col
      (view-title media)
      (view-year media)]]

    [:div.px-4.flex.flex-col.gap-4
     (view-overview media)]
    (view-videos media)

    view-gutter]
   (media-feedback/view-media-feedback-form)])

(defn view-details! [request]
  (let [media-id (-> request :request/route :media/id)
        media-db (-> request :media-db/media-db)
        media (media-db/get! media-db media-id)]
    (view-details media)))

(defmethod moviefinder-app.handle/hx-get :route/media-details [request]
  (-> request
      (handle/html view-details!)))