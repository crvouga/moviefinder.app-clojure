(ns moviefinder-app.login
  (:require [moviefinder-app.requests]
            [moviefinder-app.route]
            [moviefinder-app.user-session.db]
            [moviefinder-app.user-session.db-impl]
            [moviefinder-app.view]))


(defn send-login-with-email-link! [input]
  (let [email-address (-> input :email-address)]
    (println "Sending login link to" email-address)))

(defmethod moviefinder-app.requests/handle-hx :user-session/submitted-send-login-link [_request]
  (send-login-with-email-link! _request)
  (moviefinder-app.requests/html [:div "Login link sent"]))

(defn view-login-with-email-form [_request]
  [:form.flex.flex-col.gap-4.w-full
   {:hx-post (-> {:route/name :user-session/submitted-send-login-link}
                 moviefinder-app.route/encode)
    :hx-target "none"
    :hx-indicator "#login-with-email-indicator"}
   [:input.bg-black {:type "email"
                     :name "email-address"
                     :placeholder "Email Address"}]
   (moviefinder-app.view/button
    {:type "submit"
     :button/hx-indicator-id "#login-with-email-indicator"}
    "Send login link")])

(defn view-login-screen [request]
  [:div.w-full.h-full.flex.flex-1.flex-col
   (moviefinder-app.view/top-bar {:top-bar/title "Login"})
   [:div.flex-1.w-full.p-6.flex.flex-col.items-center.justify-center
    (view-login-with-email-form request)]])

(defn view-login [request]
  (moviefinder-app.view/view-app-tabs-layout
   {:route/name :account/account}
   (view-login-screen request)))

(defmethod moviefinder-app.requests/handle-hx :user-session/login [request]
  (-> request
      view-login
      moviefinder-app.requests/html))