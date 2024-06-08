(ns moviefinder-app.login.send-login-link
  (:require [moviefinder-app.email.send-email :as send-email]
           [moviefinder-app.env]
           [moviefinder-app.login.login-link :as login-link]
           [moviefinder-app.login.login-link-db-impl]
           [moviefinder-app.login.login-link-db :as login-link-db]
           [moviefinder-app.requests]
           [moviefinder-app.route]
           [moviefinder-app.user-session.user-session-db-impl]
           [moviefinder-app.view]
           [moviefinder-app.view.icon]))


(def base-url (moviefinder-app.env/get-env-var! "BASE_URL"))

(defn prepend-base-url [pathname]
  (str base-url pathname))

(defn ->login-link-route [login-link]
  {:route/name :login/clicked-login-link
   :login-link/id (login-link :login-link/id)})

(defn view-login-link-email-body [login-link]
  (moviefinder-app.view/button
   {:href (-> login-link ->login-link-route moviefinder-app.route/encode prepend-base-url)
    :button/element :a
    :button/label "Login"}))

(defn ->login-link-email [login-link]
  {:email/to (-> login-link :login-link/email)
   :email/subject "Login to moviefinder.app"
   :email/body-view (view-login-link-email-body login-link)})

(defn send-login-link! [input]
  (let [login-link-db (-> input :login-link-db/login-link-db)
        send-email (-> input :send-email/send-email)
        email (-> input :login/email)
        login-link (login-link/new! email)
        login-link-email (->login-link-email login-link)]
    (send-email/send-email! send-email login-link-email)
    (login-link-db/put! login-link-db #{login-link})
    login-link))

(defn view-login-email-sent [_request]
  [:div.flex.gap-3.flex-col.w-full
   (moviefinder-app.view.icon/checkmark-circle {:class "size-20 text-green-500 -ml-2"})
   [:h1.text-3xl.font-bold. "Email sent"]
   [:p.opacity-80 "We've sent you an email with a link to login with."]
   [:div.w-full.py-2]
   [:a.text-underline.opacity-80.underline
    {:href (-> {:route/name :route/login} moviefinder-app.route/encode)
     :hx-get (-> {:route/name :route/login} moviefinder-app.route/encode)}
    "Back to login"]])

(defmethod moviefinder-app.requests/handle-hx :login/submitted-send-login-link [request]
  (-> request
      (assoc :login/email (-> request :request/form-data :email)
             :user-session/id (-> request :request/session-id))
      send-login-link!)
  (moviefinder-app.requests/html (view-login-email-sent request)))

(defn view-login-with-email-form [_request]
  [:form.flex.flex-col.gap-4.w-full
   {:method "POST"
    :hx-post (-> {:route/name :login/submitted-send-login-link} moviefinder-app.route/encode)
    :hx-swap "outerHTML"
    :hx-target "this"
    :hx-indicator "#login-with-email-indicator"}
   (moviefinder-app.view/text-field
    {:text-field/id "email"
     :text-field/label "Email Address"
     :text-field/type "email"
     :text-field/name :login/email
     :required true})

   (moviefinder-app.view/button
    {:type "submit"
     :button/label "Send login link"
     :button/hx-indicator-id "#login-with-email-indicator"})])


(defn view-login-screen [request]
  [:div.w-full.h-full.flex.flex-1.flex-col
   (moviefinder-app.view/top-bar {:top-bar/title "Login with email"})
   [:div.flex-1.w-full.p-6.flex.flex-col.items-center
    (view-login-with-email-form request)]])

(defn view-login [request]
  (moviefinder-app.view/view-app-tabs-layout
   {:route/name :route/account}
   (view-login-screen request)))

(defmethod moviefinder-app.requests/handle-hx :route/login [request]
  (-> request
      view-login
      moviefinder-app.requests/html))