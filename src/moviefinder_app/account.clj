(ns moviefinder-app.account
  (:require [moviefinder-app.requests]
            [moviefinder-app.view]
            [moviefinder-app.view.icon]))



(defn view-account-index [_]
  (moviefinder-app.view/view-app-tabs-layout 
   {:route/name :account/index}
   [:div
    [:h1 "Account"]
    [:p "This is the account page."]]))

(defmethod moviefinder-app.requests/route-hx :account/index [request]
  (moviefinder-app.requests/html (view-account-index request)))