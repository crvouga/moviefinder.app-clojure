(ns moviefinder-app.account
  (:require [moviefinder-app.handle :as handle]
            [moviefinder-app.route :as route]
            [moviefinder-app.view :as view]
            [moviefinder-app.view.icon :as icon]))


(def login-route (-> {:route/name :route/login-with-sms} route/encode))

(defn view-login-cta [_request]
  [:div.w-full.h-full.flex.items-center.justify-center.flex-col.gap-4.p-12.text-center
   (icon/door {:class "size-20"})
   [:h1.text-xl.font-bold "Login to access your account."]
   (view/button
    {:button/element :a
     :button/label "Login"
     :button/indicator-id "login-button"
     :hx-boost true
     :hx-target "#app"
     :data-loading-target "#login-button"
     :data-loading-path login-route
     :hx-get login-route
     :hx-push-url login-route
     :href login-route})])

(def logout-route 
  (-> {:route/name :route/logout} route/encode))

(defn view-logged-in [_request]
  [:div.w-full.h-full.flex.flex-col.items-center.justify-center
   (view/button {:button/label "Logout"
                 :data-loading-path logout-route
                 :hx-post logout-route})])

(defn view-account-screen [request]
  (if (-> request :user/id)
    (view-logged-in request)
    (view-login-cta request)))

(defn view-account [request]
  (view/app-tabs-layout
   {:route/name :route/account}
   (view-account-screen request)))

(defmethod handle/hx-get :route/account [request]
  (-> request
      (handle/html view-account)))