(ns app.routes
  (:require [app.view]
            [app.icon]))


(def route-feed ::feed)
(def route-counter ::counter)


(defn view-app-tabs-layout [active-route view-tab-panel]
  (app.view/tab-container
   (app.view/tab-panel view-tab-panel)
   (app.view/tabs
    (app.view/tab {:label "Feed" 
                   :active-route active-route 
                   :route route-feed 
                   :icon (app.icon/home)})
    (app.view/tab {:label "Account" 
                   :active-route active-route 
                   :route route-counter 
                   :icon (app.icon/user-circle)}))))
