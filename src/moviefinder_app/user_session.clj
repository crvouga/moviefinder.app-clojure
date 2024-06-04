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

(defmethod moviefinder-app.requests/handle-hx :user-session/clicked-send-login-link [_request]
  nil)

(defn view-login [_request]
  [:div
   [:h1 "Login"]
   [:form {:hx-post (-> {:route/name :user-session/clicked-send-login-link}
                        moviefinder-app.route/encode)}
    [:input {:type "email"
             :name "email"
             :placeholder "Email"}]
    (moviefinder-app.view/button {:type "submit"} "Send login link")]])

(defmethod moviefinder-app.requests/handle-hx :user-session/login [request]
  (-> request 
      view-login 
      moviefinder-app.requests/html))