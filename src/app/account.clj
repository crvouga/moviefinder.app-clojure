(ns app.account
  (:require [app.res]
            [app.view]
            [app.icon]))



(defn view-account-index [_]
  (app.view/view-app-tabs-layout 
   {:route/name :account/index}
   [:div
    [:h1 "Account"]
    [:p "This is the account page."]]))

(defmethod app.res/handle :account/index [request]
  (app.res/html (view-account-index request)))