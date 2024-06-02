(ns app.view
  (:require [app.res]
            [app.icon]))

(defn button
  [props & children]
  [:button.bg-blue-600.hover:bg-blue-700.text-white.font-bold.px-4.py-2.rounded.active:opacity-50 props children])

(defn tab-container [& children]
  [:div.w-full.h-full.flex.flex-col.overflow-hidden {:id "tabs"} children])

(defn tab [{:keys [route label active-route icon]}]
  [:a.flex-1.p-2.flex.items-center.justify-center.flex-col.gap-1.text-xs
   {:hx-get (app.res/encode-route route)
    :class (if (= (route :route/name) (active-route :route/name)) "bg-neutral-800" "hover:bg-neutral-800")
    :hx-target "#tabs"
    :hx-swap "innerHTML"
    :hx-push-url (app.res/encode-route route)
    :href (app.res/encode-route route)}
   icon
   label])

(defn tabs [& children]
  [:nav.flex.w-full.shrink-0.border-t.border-neutral-700.divide-x.divide-neutral-700 {} children])

(defn tab-panel [children]
  [:div.w-full.flex-1.overflow-hidden.overflow-y-scroll {} children])

(defn view-app-tabs-layout [active-route view-tab-panel]
  (app.view/tab-container
   (app.view/tab-panel view-tab-panel)
   (app.view/tabs
    (app.view/tab {:label "Feed"
                   :active-route active-route
                   :route {:route/name :feed/index}
                   :icon (app.icon/home)})
    (app.view/tab {:label "Account"
                   :active-route active-route
                   :route {:route/name :account/index}
                   :icon (app.icon/user-circle)}))))
