(ns moviefinder-app.account
  (:require [moviefinder-app.requests]
            [moviefinder-app.route]
            [moviefinder-app.view]
            [moviefinder-app.view.icon]))


(defn view-login-cta [_request]
  [:div.w-full.h-full.flex.items-center.justify-center.flex-col.gap-4.p-12.text-center
   (moviefinder-app.view.icon/door {:class "size-20"})
   [:h1.text-xl.font-bold "Login to access your account."]
   (moviefinder-app.view/button
    {:button/element :button
     :button/label "Login"
     :hx-target "#app"
     :hx-get (-> {:route/name :user-session/login}
               moviefinder-app.route/encode)})])

(defn view-account [request]
  (moviefinder-app.view/view-app-tabs-layout 
   {:route/name :account/account}
   (view-login-cta request)))

(defmethod moviefinder-app.requests/handle-hx :account/account [request]
  (moviefinder-app.requests/html (view-account request)))