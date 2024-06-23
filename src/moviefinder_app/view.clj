(ns moviefinder-app.view
  (:require [moviefinder-app.route :as route]
            [moviefinder-app.view.icon :as icon]
            [hiccup2.core :as hiccup]))

(defn view-raw-script [raw-javascript]
  [:script
   {:type "text/javascript"}
   (hiccup/raw raw-javascript)])

(def one-video-allowed-to-play-script
  "document.addEventListener('OMContentLoaded', function() {
    function ensureOneVideoPlaying() {
        const videos = document.querySelectorAll('video');

        videos.forEach(function (video) {
            video.addEventListener('play', function() {
                videos.forEach(function (otherVideo) {
                    if (otherVideo !== video) {
                        otherVideo.pause();
                    }
                });
            });
        });
    }

    // Run the function initially to set up the event listeners
    ensureOneVideoPlaying();

    // Create a MutationObserver to observe changes in the body
    const observer = new MutationObserver(function(mutationsList) {
        for (let mutation of mutationsList) {
            if (mutation.type === 'childList') {
                // Re-run the function to ensure event listeners are added to new videos
                ensureOneVideoPlaying();
            }
        }
    });

    // Start observing the body for added or removed nodes
    observer.observe(document.body, { childList: true, subtree: true });
});")

(defn html-doc [children]
  [:html {:lang "en" :doctype :html5}
   [:head
    [:title "moviefinder.app"]
    [:meta {:name :description :content "Find movies to watch"}]
    [:meta {:charset "utf-8"}]
    [:link {:rel "icon" :href "data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 36 36'><text y='32' font-size='32'>🍿</text></svg>"}]
    [:meta {:name :viewport :content "width=device-width, initial-scale=1.0"}]
    [:script {:src "https://cdn.tailwindcss.com"}]
    [:script {:src "https://unpkg.com/htmx.org@1.9.12"}]
    [:script {:src "https://cdn.jsdelivr.net/npm/swiper@11/swiper-element-bundle.min.js"}]
    [:script {:src "https://cdn.jsdelivr.net/npm/alpinejs@3.x.x/dist/cdn.min.js"}]
    (view-raw-script one-video-allowed-to-play-script)
    #_[:script {:src "https://unpkg.com/swiper/swiper-bundle.min.js"}]
    #_[:link {:rel "stylesheet" :href "https://unpkg.com/swiper/swiper-bundle.min.css"}]]

   [:body
    [:div
     {:class "bg-neutral-950 text-white fixed left-1/2 top-1/2 transform -translate-x-1/2 -translate-y-1/2 w-screen h-[100dvh] flex flex-col items-center justify-center"
      :hx-boost true 
      :hx-target "#app" 
      :hx-swap "innerHTML"}
     [:div {:id "app" :class "relative flex h-full max-h-[915px] w-full max-w-[520px] flex-col items-center overflow-hidden rounded border border-neutral-700"}
      children]]]])

(defn merge-class [props-left props-right]
  (let [class-left (-> props-left :class)
        class-right (-> props-right :class)]
    (if (and class-left class-right)
      (assoc props-left :class (str class-left " " class-right))
      props-left)))


(defn merge-props [props-left props-right]
  (let [props-left (merge-class props-left props-right)]
    (merge props-left props-right)))

(defn- alert-class [props]
  (condp = (-> props :alert/variant)
    :alert/error "bg-red-800 border border-red-500 text-white"
    "bg-neutral-800 border border-neutral-700"))

(defn alert [props]
  [:div.rounded.p-4.flex.flex-row.items-center.gap-2
   (merge {:class (alert-class props)} props)
   [:p.text-sm.opacity-80 (-> props :alert/message)]])

(defn spinner 
  ([]
   (spinner {}))
  ([props]
   (icon/spinner (merge-props {:class "animate-spin size-8"} props))))

(defn button
  [props]
  (let [element (-> props :button/element (or :button))
        hx-indicator-id (-> props :button/hx-indicator-id)
        label (-> props :button/label)
        type (-> props :button/type (or "button"))
        props-base {:class "text-center bg-blue-600 hover:bg-blue-700 text-white font-bold px-5 py-3 text-lg rounded active:opacity-50 flex items-center justify-center gap-2"
                    :type type}
        props (merge props-base props)]
    [element
     props
     (-> props :button/start)
     (when (and hx-indicator-id false)
       (spinner {:id hx-indicator-id :class "display-none"}))
     label]))

(defn text-field [input]
  [:div.w-full.flex.flex-col.gap-2.text-lg
   {:class (when (-> input :text-field/hidden? not) "display-none")}
   [:label.font-bold.text-base
    {:for (-> input :text-field/id)}
    (-> input :text-field/label)]

   [:input.border.border-neutral-600.text-white.p-4.rounded.focus:outline.bg-neutral-900
    {:id (-> input :text-field/id)
     :type (-> input :text-field/type)
     :autofocus (-> input :text-field/autofocus?)
     :name (-> input :text-field/name)
     :placeholder (-> input :text-field/placeholder)
     :required (-> input :text-field/required?)}]])

(defn tab-container [& children]
  [:div.w-full.h-full.flex.flex-col.overflow-hidden {:id "tabs"} children])

(defn tab [input]
  [:a.flex-1.p-2.flex.items-center.justify-center.flex-col.gap-1.text-xs.active:opacity-60
   {:hx-get (-> input :tab/route route/encode)
    :class (if (-> input :tab/active?) "text-blue-500" "hover:bg-neutral-800")
    :hx-target "#tabs"
    :hx-swap "innerHTML"
    :hx-push-url (-> input :tab/route route/encode)
    :href (-> input :tab/route route/encode)}
    (-> input :tab/icon)
    (-> input :tab/label)])

(defn tabs [& children]
  [:nav.flex.w-full.shrink-0.border-t.border-neutral-700 {} children])


(defn tab-panel [children]
  [:div.w-full.flex-1.overflow-hidden.overflow-y-scroll children])

(defn action-button-container [& children]
  [:div.flex.flex-row.w-full.items-center.justify-center.divide-x.divide-neutral-700.border-t.border-neutral-700
   children])


(defn action-buttton [props]
  [:button.flex-1.p-1.flex.flex-col.items-center.justify-center.gap-0.5.text-xs
   {:disabled (-> props :action-button/disabled?)
    :class (str (when (-> props :action-button/disabled? not) "hover:bg-neutral-800 ")
                (when (-> props :action-button/active?) "text-blue-500 ")
                (when (-> props :action-button/disabled?) "opacity-50 "))}
   (-> props :action-button/icon)
   (-> props :action-button/label)])

(defn toggle-button-group [props & children]
  [:div.flex.flex-row.gap-2.items-center.justify-center
   (when (-> props :toggle-button-group/label string?)
     [:label.text-sm.font-bold (-> props :toggle-button-group/label)])
   [:div.flex.w-fit.border.rounded.overflow-hidden.divide-x.border-neutral-700.divide-neutral-700 props
    children]])

(defn toggle-button [props]
  [:button.p-1.flex.flex-row.items-center.justify-center.gap-1.text-xs.p-2.aspect-square
   {:disabled (-> props :toggle-button/disabled?)
    :class (str
            "h-[48px] "
            (when (-> props :toggle-button/selected?) "bg-blue-600 ")
            (when (-> props :toggle-button/disabled? not) "hover:bg-neutral-800 ")
            (when (-> props :toggle-button/active?) "text-blue-500 ")
            (when (-> props :toggle-button/disabled?) "opacity-50 "))}
   (-> props :toggle-button/icon)
   #_(-> props :toggle-button/label)])

(defn app-tabs-layout [active-route view-tab-panel]
  (tab-container
   (tab-panel view-tab-panel)
   (tabs
    (tab {:tab/label "Home"
          :tab/active? (= (active-route :route/name) :route/home)
          :tab/route {:route/name :route/home}
          :tab/icon (icon/home)})
    (tab {:tab/label "Search"
          :tab/active? (= (active-route :route/name) :route/search)
          :tab/route {:route/name :route/search}
          :tab/icon (icon/search)})
    (tab {:tab/label "Account"
          :tab/active? (= (active-route :route/name) :route/account)
          :tab/route {:route/name :route/account}
          :tab/icon (icon/user-circle)}))))


(defn icon-button [input]
  [:button.bg-transparent.text-white.p-2.rounded-full.aspect-square
   (input :icon-button/icon)])

(defn top-bar [input]
  [:div.w-full.flex.items-center.justify-center.border-b.border-neutral-700.h-16.px-2
   [:div.flex-1
    #_(icon-button
     {:icon-button/icon (icon/arrow-left)})]
   [:h1.flex-4.text-center.font-bold.text-lg 
    (-> input :top-bar/title)]
   [:div.flex-1]])

(defn success [input]
  [:div.flex.gap-3.flex-col.w-full
   (icon/checkmark-circle {:class "size-20 text-green-500 -ml-2"})
   [:h1.text-3xl.font-bold (-> input :success/title)]
   [:p.opacity-80 (-> input :success/body)]])

(defn failure [input]
  [:div.flex.gap-3.flex-col.w-full
   (icon/exclaimation-circle {:class "size-20 text-red-500 -ml-2"})
   [:h1.text-3xl.font-bold (-> input :failure/title)]
   [:p.opacity-80 (-> input :failure/body)]])

(defn loading [props]
  [:div.flex.gap-3.flex-col.w-full.flex-1.items-center.justify-center
   props
   (spinner)])

(defn chip [props]
  [:button.flex.flex-row.items-center.justify-center.gap-2.p-2.px-4.rounded-full.bg-neutral-700.text-base
   (-> props :chip/label)])