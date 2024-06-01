(ns app.routes
  (:require [app.view]))


(def route-feed ::feed)
(def route-counter ::counter)


(defn view-app-tabs-layout [active-route view-tab-panel]
  (app.view/tab-container
   (app.view/tab-panel view-tab-panel)
   (app.view/tabs
    (app.view/tab route-counter "Counter" active-route)
    (app.view/tab route-feed "Feed" active-route))))
