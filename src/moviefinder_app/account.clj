(ns moviefinder-app.account
  (:require [moviefinder-app.handle]
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
     :hx-push-url (-> {:route/name :route/login}
                      moviefinder-app.route/encode)
     :hx-get (-> {:route/name :route/login}
                 moviefinder-app.route/encode)})])

(defn view-logged-in [_request]
  [:div "Logged in"])

(defn view-account-screen [request]
  (if (-> request :user/id)
    (view-logged-in request)
    (view-login-cta request)))

(defn view-account [request]
  (moviefinder-app.view/app-tabs-layout 
   {:route/name :route/account}
   (view-account-screen request)))

(defmethod moviefinder-app.handle/handle-hx :route/account [request]
  (moviefinder-app.handle/html (view-account request)))