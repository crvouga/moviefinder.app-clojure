(ns moviefinder-app.account
  (:require [moviefinder-app.requests]
            [moviefinder-app.view]
            [moviefinder-app.view.icon]))



(defn view-account [_]
  (moviefinder-app.view/view-app-tabs-layout 
   {:route/name :account/acount}
   [:div
    [:h1 "Account"]
    [:p "This is the account page."]]))

(defmethod moviefinder-app.requests/handle-hx :account/acount [request]
  (moviefinder-app.requests/html (view-account request)))