(ns app.feed
  (:require [app.res]
            [app.view]
            [app.routes]))

(defn view-feed-panel []
  [:div [:h1 "Feed"]])

(defn view-feed-route []
  (app.routes/view-app-tabs-layout app.routes/route-feed (view-feed-panel)))

(defmethod app.res/req->res app.routes/route-feed [_]
  (app.res/html (view-feed-route)))

