(ns moviefinder-app.login.login-with-email.use-login-link
  (:require [moviefinder-app.error :refer [ex err->msg]]
            [moviefinder-app.handle :as handle]
            [moviefinder-app.login.login-with-email.login-link.login-link :as login-link]
            [moviefinder-app.login.login-with-email.login-link.login-link-db :as login-link-db]
            [moviefinder-app.route :as route]
            [moviefinder-app.user-session.user-session-db :as user-session-db]
            [moviefinder-app.user.user :as user]
            [moviefinder-app.user.user-db :as user-db]
            [moviefinder-app.view :as view]
            [moviefinder-app.view.icon :as icon]))

(defn- assoc-login-link! [input]
  (let [login-link-db (input :login-link-db/login-link-db)
        login-link-id (input :login-link/id)
        login-link (->> login-link-id (login-link-db/find-by-id! login-link-db) first)]
    (assoc input ::login-link login-link)))

(defn- validate-link-found [input]
  (let [login-link (-> input ::login-link)]
    (when-not login-link
      (throw (ex :err/login-link-not-found input)))
    input))

(defn- validate-link-not-used [input]
  (let [login-link (-> input ::login-link)]
    (when (login-link/used? login-link)
      (throw (ex :err/login-link-already-used input)))
    input))

(defn- validate-link-not-expired [input]
  (let [login-link (-> input ::login-link)]
    (when (login-link/expired? login-link)
      (throw (ex :err/login-link-expired input)))
    input))

(defn- mark-login-link-as-used [input]
  (update input ::login-link login-link/mark-as-used))

(defn- assoc-user! [input]
  (let [login-link (-> input ::login-link)
        user-email (-> login-link :login-link/email)
        user-db (-> input :user-db/user-db)
        maybe-user (first (user-db/find-by-email! user-db user-email))
        user (if maybe-user maybe-user (user/new! {:user/email user-email}))]
    (assoc input ::user user)))

(defn validate-user-session-id-exists [input]
  (let [session-id (-> input :session/id)]
    (when-not session-id
      (throw (ex :err/user-session-id-not-associate-with-request input)))
    input))

(defn- assoc-user-session [input]
  (let [user (-> input ::user)
        session-id (-> input :session/id)
        user-id (:user/id user)
        user-session {:session/id session-id
                      :user/id user-id}]
    (assoc input ::user-session user-session)))

(defn- put-user-session! [input]
  (let [user-session-db (-> input :user-session-db/user-session-db)
        user-session (-> input ::user-session)]
    (user-session-db/put! user-session-db #{user-session})
    input))

(defn- put-user! [input]
  (let [user-db (-> input :user-db/user-db)
        user (-> input ::user)]
    (user-db/put! user-db #{user})
    input))

(defn- put-login-link! [input]
  (let [login-link-db (-> input :login-link-db/login-link-db)
        login-link (-> input ::login-link)]
    (login-link-db/put! login-link-db #{login-link})
    input))

(defn use-login-link! [input]
  (-> input
      assoc-login-link!
      validate-link-found
      validate-link-not-expired
      validate-link-not-used
      mark-login-link-as-used
      assoc-user!
      validate-user-session-id-exists
      assoc-user-session
      put-user!
      put-user-session!
      put-login-link!))



(def view-top-bar
  (view/top-bar {:top-bar/title "Login with email"}))

(def view-back-to-app
  [:div.pt-6.w-fit
   (view/button {:button/label "Back to app"
                 :button/start (icon/arrow-left)
                 :button/element :a
                 :href (-> {:route/name :route/home} route/encode)})])

(defn- view-use-login-link-ok [_request]
  [:div.w-full.flex.flex-col
   view-top-bar
   [:div.flex-1.w-full.p-6.flex.flex-col
    (view/success {:success/title "Logged in"
                   :success/body "You have successfully logged in."})
    view-back-to-app]])

(defmethod handle/hx-get :route/use-login-link-ok [request]
  (-> request 
      (handle/html (comp view/html-doc view-use-login-link-ok))))

(defmethod err->msg :err/login-link-not-found [_ex]
  "Login link was not found. Please request a new one")

(defmethod err->msg :err/login-link-already-used [_ex]
  "Login link has already been used. Please request a new one")

(defmethod err->msg :err/login-link-expired [_ex]
  "Login link has expired. Please request a new one")

(defmethod err->msg :err/user-session-id-not-associate-with-request [_ex]
  "User session ID not found")

(defmethod err->msg :default [_ex]
  "An error occurred")

(defn- view-use-login-link-err [request]
  [:div.w-full.flex.flex-col
   view-top-bar
   [:div.flex-1.w-full.p-6.flex.flex-col
    (view/failure {:failure/title (err->msg (:err/err request))})
    view-back-to-app]])

(defmethod handle/handle :route/use-login-link-ok [request]
  (-> request 
      (handle/html (comp view/html-doc view-use-login-link-ok))))

(defmethod handle/handle :route/use-login-link [request]
  (try
    (-> request (merge (:request/route request)) use-login-link!)
    (handle/redirect {:route/name :route/use-login-link-ok})
    (catch Exception ex
      (-> request
          (assoc :err/err (ex-data ex)) 
          (handle/html (comp view/html-doc view-use-login-link-err))))))