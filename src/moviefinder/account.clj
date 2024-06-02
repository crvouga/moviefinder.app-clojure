(ns moviefinder.account
  (:require [moviefinder.requests]
            [moviefinder.view]
            [moviefinder.view.icon]))



(defn view-account-index [_]
  (moviefinder.view/view-app-tabs-layout 
   {:route/name :account/index}
   [:div
    [:h1 "Account"]
    [:p "This is the account page."]]))

(defmethod moviefinder.requests/route-hx :account/index [request]
  (moviefinder.requests/html (view-account-index request)))