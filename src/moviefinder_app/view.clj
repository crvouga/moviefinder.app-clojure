(ns moviefinder-app.view
  (:require [moviefinder-app.route :as route]
            [moviefinder-app.view.icon :as icon]
            [hiccup2.core :as hiccup]))

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
    [:script {:src "https://cdn.jsdelivr.net/npm/swiper@11/swiper-element-bundle.min.js"}]]

   [:body.bg-neutral-950.text-white {:hx-boost true :hx-target "#app" :hx-swap "innerHTML"}
    [:div
     {:class "fixed left-1/2 top-1/2 transform -translate-x-1/2 -translate-y-1/2 w-screen h-[100dvh] flex flex-col items-center justify-center"}
     [:div {:id "app" :class "relative flex h-full max-h-[915px] w-full max-w-[520px] flex-col items-center overflow-hidden rounded border border-neutral-700"}
      children]]]])

(defn spinner 
  ([]
   (spinner {}))
  ([props]
   (icon/spinner (merge {:class "animate-spin size-8"} props))))

(defn button
  [props]
  (let [element (-> props :button/element (or :button))
        hx-indicator-id (-> props :button/hx-indicator-id)
        label (-> props :button/label)
        props-base {:class "text-center bg-blue-600 hover:bg-blue-700 text-white font-bold px-5 py-3 text-lg rounded active:opacity-50 flex items-center justify-center gap-2"}
        props (merge props-base props)]
    [element
     props
     (-> props :button/start)
     (when (and hx-indicator-id false)
       (spinner {:id hx-indicator-id}))
     label]))

(defn text-field [input]
  [:div.w-full.flex.flex-col.gap-2
   [:label.font-bold.text-base
    {:for (-> input :text-field/id)}
    (-> input :text-field/label)]

   [:input.border.border-neutral-600.text-white.p-4.rounded.focus:outline.bg-neutral-900
    {:id (-> input :text-field/id)
     :type (-> input :text-field/type)
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
  [:nav.flex.w-full.shrink-0.border-t.border-neutral-700.divide-x.divide-neutral-700 {} children])

(defn tab-panel [children]
  [:div.w-full.flex-1.overflow-hidden.overflow-y-scroll {} children])


(defn app-tabs-layout [active-route view-tab-panel]
  (tab-container
   (tab-panel view-tab-panel)
   (tabs
    (tab {:tab/label "Feed"
          :tab/active? (= (active-route :route/name) :route/home)
          :tab/route {:route/name :route/home}
          :tab/icon (icon/home)})
    (tab {:tab/label "Account"
          :tab/active? (= (active-route :route/name) :route/account)
          :tab/route {:route/name :route/account}
          :tab/icon (icon/user-circle)}))))

(defn icon-button [input]
  [:button.bg-transparent.text-white.p-2.rounded-full
   (input :icon-button/icon)])


(defn top-bar [input]
  [:div.w-full.flex.items-center.justify-center.border-b.border-neutral-700.h-16.px-2
   [:div.flex-1
    #_(icon-button
     {:icon-button/icon (icon/arrow-left)})]
   [:h1.flex-4.text-center.font-bold.text-lg 
    (-> input :top-bar/title)]
   [:div.flex-1]])

(defn view-raw-script [raw-javascript]
  [:script
   {:type "text/javascript"}
   (hiccup/raw raw-javascript)])

(defn success [input]
  [:div.flex.gap-3.flex-col.w-full
   (icon/checkmark-circle {:class "size-20 text-green-500 -ml-2"})
   [:h1.text-3xl.font-bold (-> input :success/title)]
   [:p.opacity-80 (-> input :success/body)]])