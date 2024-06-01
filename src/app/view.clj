(ns app.view
  (:require [app.res]))

(defn button
  [props & children]
  [:button.bg-blue-600.hover:bg-blue-700.text-white.font-bold.px-4.py-2.rounded.active:opacity-50 props children])

(defn tab [route label]
  [:a.flex-1.p-4.flex.items-center.justify-center
   {:hx-get (app.res/route->url route)
    :hx-target "#tab"
    :hx-swap "innerHTML"
    :hx-push-url (app.res/route->url route)}  
   label])

(defn tabs [& children]
  [:nav.flex.w-full.shrink-0.border-t.border-neutral-700.divide-x.divide-neutral-700 {} children])

(defn tab-panel [children]
  [:div#tab.w-full.flex-1.overflow-y-scroll {} children])