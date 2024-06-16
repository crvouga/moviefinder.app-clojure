(ns moviefinder-app.media.media-details
  (:require [moviefinder-app.handle :as handle]
            [moviefinder-app.view :as view]
            [moviefinder-app.media.media-db :as media-db]
            [moviefinder-app.media-feedback.media-feedback :as media-feedback]))

(defn year [maybe-date-string]
  (when maybe-date-string
    (let [match (re-find #"(\d{4})" maybe-date-string)]
      (if match
        (Integer/parseInt (first match))
        nil))))

(defn view-backdrop [media]
  [:img.w-full.aspect-video.bg-neutral-900.absolute.top-0.left-0.-z-10.blur-sm {:src (-> media :media/backdrop-url)}])

(defn view-poster [media]
  [:img.aspect-auto.bg-neutral-900.rounded.shadow-xl.mx-auto {:class "w-1/2" :src (-> media :media/poster-url)}])

(defn view-title [media]
  [:h1.font-bold.text-3xl.text-center (-> media :media/title)])

(defn view-year [media]
  [:h2.text-lg.text-center.text-neutral-300 (-> media :media/release-date year)])

(defn view-overview [media]
  [:p.text-neutral-300.text-sm (-> media :media/overview)])

(def view-gutter [:div.w-full.p-8])

(defn view-section-title [title]
  [:h2.text-xl.text-left.font-bold title])

(defn view-video-link [video]
  [:a.text-sm.text-neutral-300.underline
   {:href (-> video :video/youtube-watch-url)
    :target "_blank"
    :rel "noopener noreferrer"}
   (-> video :video/name)])

(defn view-videos [media]
  [:div
   (view-section-title "Videos")
   [:ul.flex.flex-col.gap-2
    (for [video (-> media :media/videos)]
      [:li
       (view-video-link video)])]])

(defn view-details [media]
  [:div.w-full.flex.flex-col.flex-1.overflow-hidden
   (moviefinder-app.view/top-bar {:top-bar/title (-> media :media/title)})
   [:div.w-full.flex.flex-col.flex-1.relative.pt-14.overflow-y-scroll.p-4.gap-4
    (view-backdrop media)
    (view-poster media)
    (view-title media)
    (view-year media)
    (view-overview media)
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