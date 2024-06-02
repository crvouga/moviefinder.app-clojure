(ns app.account
  (:require [app.requests]
            [app.view]
            [app.icon]))



(defn view-account-index [_]
  (app.view/view-app-tabs-layout 
   {:route/name :account/index}
   [:div
    [:h1 "Account"]
    [:p "This is the account page."]]))

(defmethod app.requests/route-hx :account/index [request]
  (app.requests/html (view-account-index request)))