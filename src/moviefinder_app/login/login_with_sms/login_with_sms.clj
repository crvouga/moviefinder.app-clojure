(ns moviefinder-app.login.login-with-sms.login-with-sms
  (:require [moviefinder-app.handle :as handle]
            [moviefinder-app.view :as view]))

(defmulti view-step (fn [request] (-> request :request/route :login-with-sms/step)))


(defn- view-login-with-sms [request]
  (view/app-tabs-layout
   {:route/name :route/account}
   [:div.w-full.flex.flex-col
    (view/top-bar {:top-bar/title "Login with phone"})
    [:div.w-full.flex-1.p-6
     (view-step request)]]))

(defmethod handle/handle-hx :route/login-with-sms [request]
  (-> request
      view-login-with-sms
      handle/html))