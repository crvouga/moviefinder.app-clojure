(ns moviefinder-app.account
  (:require [moviefinder-app.handle :as handle]
            [moviefinder-app.route :as route]
            [moviefinder-app.view :as view]
            [moviefinder-app.view.icon :as icon]))


(defn view-login-cta [_request]
  [:div.w-full.h-full.flex.items-center.justify-center.flex-col.gap-4.p-12.text-center
   (icon/door {:class "size-20"})
   [:h1.text-xl.font-bold "Login to access your account."]
   (view/button
    {:button/element :a
     :button/label "Login"
     :hx-boost true
     :href (-> {:route/name :route/login-with-sms}
               route/encode)})])

(defn view-logged-in [_request]
  [:div.w-full.h-full.flex.flex-col.items-center.justify-center
   (view/button {:button/label "Logout"
                 :hx-post (-> {:route/name :route/logout} route/encode)})])

(defn view-account-screen [request]
  (if (-> request :user/id)
    (view-logged-in request)
    (view-login-cta request)))

(defn view-account [request]
  (view/app-tabs-layout
   {:route/name :route/account}
   (view-account-screen request)))

(defmethod handle/handle-hx :route/account [request]
  (handle/html (view-account request)))