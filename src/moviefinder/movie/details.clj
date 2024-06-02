(ns moviefinder.movie.details
  (:require [moviefinder.requests]
            [moviefinder.view]
            [moviefinder.route]
            [moviefinder.movie.db]
            [moviefinder.movie.db-impl :refer [movie-db]]))

(defn year [maybe-date-string]
  (when maybe-date-string
    (let [match (re-find #"(\d{4})" maybe-date-string)]
      (if match
        (Integer/parseInt (first match))
        nil))))

(defn view-backdrop [movie]
  [:img.w-full.aspect-video.bg-neutral-900.absolute.top-0.left-0.-z-10.blur-sm {:src (-> movie :movie/backdrop-url)}])

(defn view-poster [movie]
  [:img.aspect-auto.bg-neutral-900.rounded.shadow-xl.mx-auto {:class "w-1/2" :src (-> movie :movie/poster-url)}])

(defn view-title [movie]
  [:h1.font-bold.text-3xl.text-center (-> movie :movie/title)])

(defn view-year [movie]
  [:h2.text-lg.text-center.text-neutral-300 (-> movie :movie/release-date year)])

(defn view-overview [movie]
  [:p.text-neutral-300.text-sm (-> movie :movie/overview)])

(def view-gutter [:div.w-full.p-8])

(defn view-section-title [title]
  [:h2.text-xl.text-left.font-bold title])

(defn view-video-link [video]
  [:a.text-sm.text-neutral-300.underline
   {:href (-> video :video/youtube-watch-url)
    :target "_blank"
    :rel "noopener noreferrer"}
   (-> video :video/name)])

(defn view-videos [movie]
  [:div 
   (view-section-title "Videos")
   [:ul.flex.flex-col.gap-2
    (for [video (-> movie :movie/videos)]
      [:li
       (view-video-link video)])]])

(defn view-movie-details [movie]
  [:div.w-full.flex.flex-col.h-full.flex-1
   (moviefinder.view/top-bar {:top-bar/title (-> movie :movie/title)})
   [:div.w-full.flex.flex-col.h-full.flex-1.relative.pt-14.overflow-y-scroll.p-4.gap-4
    (view-backdrop movie)
    (view-poster movie)
    (view-title movie)
    (view-year movie)
    (view-overview movie)
    (view-videos movie)
    view-gutter]])
  
(defn view-movie-details! [request]
  (let [movie-id (-> request :request/route :movie/id)
        movie (moviefinder.movie.db/get! movie-db movie-id)]
    (view-movie-details movie)))

(defmethod moviefinder.requests/route-hx :movie/detail [request]
  (moviefinder.requests/html (view-movie-details! request)))