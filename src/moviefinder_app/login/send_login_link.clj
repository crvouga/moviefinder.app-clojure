(ns moviefinder-app.login.send-login-link
  (:require [moviefinder-app.email.send-email :as send-email]
            [moviefinder-app.env :as env]
            [moviefinder-app.login.login-link :as login-link]
            [moviefinder-app.login.login-link-db :as login-link-db]
            [moviefinder-app.handle :as handle]
            [moviefinder-app.route :as route]
            [moviefinder-app.view :as view]))


(def base-url (env/get! "BASE_URL"))

(defn- prepend-base-url [pathname]
  (str base-url pathname))

(defn ->login-link-route [login-link]
  {:route/name :route/use-login-link
   :login-link/id (login-link :login-link/id)})

(defn- view-login-link-email-body [login-link]
  (view/button
   {:href (-> login-link ->login-link-route route/encode prepend-base-url)
    :button/element :a
    :button/label "Login"}))

(defn- ->login-link-email [login-link]
  {:email/to (-> login-link :login-link/email)
   :email/subject "Login to moviefinder.app"
   :email/body-view (view-login-link-email-body login-link)})

(defn- assoc-login-link [input]
  (let [email (-> input :send-login-link/email)
        login-link (login-link/new! email)]
    (assoc input ::login-link login-link)))

(defn- assoc-login-link-email [input]
  (let [login-link (-> input ::login-link)
        login-link-email (->login-link-email login-link)]
    (assoc input ::login-link-email login-link-email)))

(defn- send-login-link-email! [input]
  (let [send-email (-> input :send-email/send-email)
        login-link-email (-> input ::login-link-email)]
    (send-email/send-email! send-email login-link-email)
    input))

(defn- put-login-link! [input]
  (let [login-link-db (-> input :login-link-db/login-link-db)
        login-link (-> input ::login-link)]
    (login-link-db/put! login-link-db #{login-link})
    input))

(defn send-login-link! [input]
  (-> input
      assoc-login-link
      assoc-login-link-email
      send-login-link-email!
      put-login-link!
      ::login-link))

(defn view-send-login-link-ok [_request]
  [:div.flex.flex-col.w-full
   (view/success {:success/title "Email sent"
                  :success/body "We've sent you an email with a link to login with."})
   [:div.w-full.py-2]
   [:a.text-underline.opacity-80.underline
    {:href (-> {:route/name :route/login} route/encode)
     :hx-get (-> {:route/name :route/login} route/encode)}
    "Back to login"]])

(defmethod handle/handle-hx :route/send-login-link [request]
  (-> request
      (assoc :send-login-link/email (-> request :request/form-data :email))
      send-login-link!)
  (handle/html (view-send-login-link-ok request)))

(defn view-send-login-link-form [_request]
  [:form.flex.flex-col.gap-4.w-full
   {:method "POST"
    :hx-post (-> {:route/name :route/send-login-link} route/encode)
    :hx-swap "outerHTML"
    :hx-target "this"
    :hx-indicator "#login-with-email-indicator"}
   (view/text-field
    {:text-field/id "email"
     :text-field/label "Email Address"
     :text-field/type "email"
     :text-field/name "email"
     :text-field/required? true})

   (view/button
    {:type "submit"
     :button/label "Send login link"
     :button/hx-indicator-id "login-with-email-indicator"})])


(defn view-login-screen [request]
  [:div.w-full.h-full.flex.flex-1.flex-col
   (view/top-bar {:top-bar/title "Login with email"})
   [:div.flex-1.w-full.p-6.flex.flex-col.items-center
    (view-send-login-link-form request)]])

(defn view-login [request]
  (view/app-tabs-layout
   {:route/name :route/account}
   (view-login-screen request)))

(defmethod handle/handle-hx :route/login [request]
  (-> request
      view-login
      handle/html))
