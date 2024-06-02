(ns moviefinder.view
  (:require [moviefinder.requests]
            [moviefinder.route]
            [moviefinder.view.icon]))

(defn button
  [props & children]
  [:button.bg-blue-600.hover:bg-blue-700.text-white.font-bold.px-4.py-2.rounded.active:opacity-50 props children])

(defn tab-container [& children]
  [:div.w-full.h-full.flex.flex-col.overflow-hidden {:id "tabs"} children])

(defn tab [input]
  [:a.flex-1.p-2.flex.items-center.justify-center.flex-col.gap-1.text-xs.active:opacity-60
   {:hx-get (-> input :tab/route moviefinder.route/encode)
    :class (if (-> input :tab/active?) "text-blue-500" "hover:bg-neutral-800")
    :hx-target "#tabs"
    :hx-swap "innerHTML"
    :hx-push-url (-> input :tab/route moviefinder.route/encode)
    :href (-> input :tab/route moviefinder.route/encode)}
    (-> input :tab/icon)
    (-> input :tab/label)])

(defn tabs [& children]
  [:nav.flex.w-full.shrink-0.border-t.border-neutral-700.divide-x.divide-neutral-700 {} children])

(defn tab-panel [children]
  [:div.w-full.flex-1.overflow-hidden.overflow-y-scroll {} children])

(defn view-app-tabs-layout [active-route view-tab-panel]
  (moviefinder.view/tab-container
   (moviefinder.view/tab-panel view-tab-panel)
   (moviefinder.view/tabs
    (moviefinder.view/tab {:tab/label "Feed"
                   :tab/active? (= (active-route :route/name) :home/home)
                   :tab/route {:route/name :home/home}
                   :tab/icon (moviefinder.view.icon/home)})
    (moviefinder.view/tab {:tab/label "Account"
                   :tab/active? (= (active-route :route/name) :account/index)
                   :tab/route {:route/name :account/index}
                   :tab/icon (moviefinder.view.icon/user-circle)}))))

(defn icon-button [input]
  [:button.bg-transparent.text-white.p-2.rounded-full
   (input :icon-button/icon)])

(defn top-bar [input]
  [:div.w-full.flex.items-center.justify-center.border-b.border-neutral-700.h-16.px-2
   [:div.flex-1
    #_(icon-button
     {:icon-button/icon (moviefinder.view.icon/arrow-left)})]
   [:h1.flex-4.text-center.font-bold.text-lg 
    (-> input :top-bar/title)]
   [:div.flex-1]])