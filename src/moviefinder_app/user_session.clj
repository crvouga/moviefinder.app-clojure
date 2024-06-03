(ns moviefinder-app.user-session
  (:require [moviefinder-app.requests]
            [moviefinder-app.route]
            [moviefinder-app.view]))

(defn assoc-user-session! [request] 
  (-> request :user-session/session-id))

(defn guard-auth! [request view-logged-out view-logged-in]
  (if (-> request :request/session-id)
    view-logged-in
    view-logged-out))

(defmethod moviefinder-app.requests/route-hx :user-session/clicked-send-login-link [_request]
  nil)

(defmethod moviefinder-app.requests/route-hx :user-session/login [_request]
  (moviefinder-app.requests/html
   [:div
    [:h1 "Login"]
    [:form {:hx-post (moviefinder-app.route/encode {:route/name :user-session/clicked-send-login-link})}
     [:input {:type "email" :name "email" :placeholder "Email"}]
     (moviefinder-app.view/button {:type "submit"} "Send login link")]]))