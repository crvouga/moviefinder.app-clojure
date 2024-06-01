(ns app.view
  (:require [app.res]))

(defn button
  [props & children]
  [:button.bg-blue-600.hover:bg-blue-700.text-white.font-bold.px-4.py-2.rounded.active:opacity-50 props children])

(defn tab-container [& children]
  [:div.w-full.h-full.flex.flex-col.overflow-hidden {:id "tabs"} children])

(defn tab [route label active-route]
  [:a.flex-1.p-4.flex.items-center.justify-center
   {:hx-get (app.res/keyword->url route)
    :class (if (= route active-route) "bg-neutral-800" "hover:bg-neutral-800")
    :hx-target "#tabs"
    :hx-swap "innerHTML"
    :hx-push-url (app.res/keyword->url route)
    :href (app.res/keyword->url route)}  
   label])

(defn tabs [& children]
  [:nav.flex.w-full.shrink-0.border-t.border-neutral-700.divide-x.divide-neutral-700 {} children])

(defn tab-panel [children]
  [:div.w-full.flex-1.overflow-hidden.overflow-y-scroll {} children])