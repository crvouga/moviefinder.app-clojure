(ns app.user-session
  (:require [app.requests]
            [app.route]
            [app.view]))

(defn assoc-user-session! [request] 
  (-> request :user-session/session-id))

(defmethod app.requests/route-hx :user-session/clicked-send-login-link [_request]
  nil)

(defmethod app.requests/route-hx :user-session/login [_request]
  (app.requests/html
   [:div
    [:h1 "Login"]
    [:form {:hx-post (app.route/encode {:route/name :user-session/clicked-send-login-link})}
     [:input {:type "email" :name "email" :placeholder "Email"}]
     (app.view/button {:type "submit"} "Send login link")]]))