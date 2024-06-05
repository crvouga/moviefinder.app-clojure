(ns moviefinder-app.user-session
  (:require [moviefinder-app.requests]
            [moviefinder-app.route]
            [moviefinder-app.view]
            [moviefinder-app.user-session.db]
            [moviefinder-app.user-session.db-impl]))

(def user-session-db
  (moviefinder-app.user-session.db/->UserSessionDb
   {:user-session-db/impl :user-session-db/impl-in-memory}))

(defn assoc-user-session! [request] 
  (let [user-session (->> request
                          :user-session/session-id
                          (moviefinder-app.user-session.db/find-user-id-by-session-id! user-session-db))]
    (-> request
        (assoc :user-session/user-session user-session))))

(defn guard-auth! [request view-logged-out view-logged-in]
  (let [user-session (-> request :user-session/user-session)]
    (if user-session
      (view-logged-in user-session)
      (view-logged-out))))


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
     :button/indicator "#login-with-email-indicator"}
    "Send login link")])

(defn view-login [request]
  [:div.w-full.h-full.flex.flex-1.flex-col
   (moviefinder-app.view/top-bar {:top-bar/title "Login"})
   [:div.flex-1.w-full.p-6.flex.flex-col.items-center.justify-center
    (view-login-with-email-form request)]])

(defmethod moviefinder-app.requests/handle-hx :user-session/login [request]
  (-> request
      view-login
      moviefinder-app.requests/html))