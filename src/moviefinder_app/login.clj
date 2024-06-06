(ns moviefinder-app.login
  (:require [moviefinder-app.requests]
            [moviefinder-app.route]
            [moviefinder-app.user-session.db]
            [moviefinder-app.user-session.db-impl]
            [moviefinder-app.view]
            [moviefinder-app.view.icon]))



(defn send-login-with-email-link! [input]
  (let [email-address (-> input :email-address)]
    (println "Sending login link to" email-address)))

(defn view-login-email-sent [_request]
  [:div.flex.gap-3.flex-col.w-full
   (moviefinder-app.view.icon/checkmark-circle {:class "size-20 text-green-500 -ml-2"})
   [:h1.text-3xl.font-bold. "Email sent"]
   [:p.opacity-80 "We've sent you an email with a link to login with."]
   [:div.w-full.py-2]
   [:a.text-underline.opacity-80.underline
    {:href (-> {:route/name :login/login} moviefinder-app.route/encode)}
    "Back to login"]])

(defmethod moviefinder-app.requests/handle-hx :login/submitted-send-login-link [request]
  (send-login-with-email-link! request)
  (moviefinder-app.requests/html (view-login-email-sent request)))

(defn view-login-with-email-form [_request]
  [:form.flex.flex-col.gap-4.w-full
   {:method "POST"
    :hx-post (-> {:route/name :login/submitted-send-login-link} moviefinder-app.route/encode)
    :hx-swap "outerHTML"
    :hx-target "this"
    :hx-indicator "#login-with-email-indicator"}
   (moviefinder-app.view/text-field
    {:text-field/id "email-address"
     :text-field/label "Email Address"
     :text-field/type "email"
     :text-field/name "email-address"
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
   {:route/name :account/account}
   (view-login-screen request)))

(defmethod moviefinder-app.requests/handle-hx :login/login [request]
  (-> request
      view-login
      moviefinder-app.requests/html))