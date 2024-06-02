(ns moviefinder.user-session
  (:require [moviefinder.requests]
            [moviefinder.route]
            [moviefinder.view]))

(defn assoc-user-session! [request] 
  (-> request :user-session/session-id))

(defn guard-auth! [request view-logged-out view-logged-in]
  (if (-> request :request/session-id)
    view-logged-in
    view-logged-out))

(defmethod moviefinder.requests/route-hx :user-session/clicked-send-login-link [_request]
  nil)

(defmethod moviefinder.requests/route-hx :user-session/login [_request]
  (moviefinder.requests/html
   [:div
    [:h1 "Login"]
    [:form {:hx-post (moviefinder.route/encode {:route/name :user-session/clicked-send-login-link})}
     [:input {:type "email" :name "email" :placeholder "Email"}]
     (moviefinder.view/button {:type "submit"} "Send login link")]]))