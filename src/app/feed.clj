(ns app.feed
  (:require [app.res]))


(defn view-feed []
  [:div
   [:h1 "Feed"]])

(defmethod app.res/req->res (str ::feed) [_]
  (app.res/html (view-feed)))