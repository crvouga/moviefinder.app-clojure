(ns moviefinder-app.login.use-login-link
  (:require [moviefinder-app.error :refer [err ex->err-type]]
            [moviefinder-app.login.login-link :as login-link]
            [moviefinder-app.login.login-link-db :as login-link-db]
            [moviefinder-app.login.login-link-db-impl]
            [moviefinder-app.requests :as requests]
            [moviefinder-app.user-session.user-session-db :as user-session-db]
            [moviefinder-app.user-session.user-session-db-impl]
            [moviefinder-app.user.user :as user]
            [moviefinder-app.user.user-db :as user-db]))

(defn- assoc-login-link! [input]
  (let [login-link-db (input :login-link-db/login-link-db)
        login-link-id (input :login-link/id)
        login-link (->> login-link-id (login-link-db/find-by-id! login-link-db) first)]
    (assoc input ::login-link login-link)))

(defn- validate-link-found [input]
  (let [login-link (-> input ::login-link)]
    (when-not login-link
      (throw (err :use-login-link-err/login-link-not-found input)))
    input))

(defn- validate-link-not-used [input]
  (let [login-link (-> input ::login-link)]
    (when (login-link/used? login-link)
      (throw (err :use-login-link-err/login-link-already-used input)))
    input))

(defn- validate-link-not-expired [input]
  (let [login-link (-> input ::login-link)]
    (when (login-link/expired? login-link)
      (throw (err :use-login-link-err/login-link-expired input)))
    input))

(defn- mark-login-link-as-used [input]
  (update input ::login-link login-link/mark-as-used))

(defn- assoc-user! [input]
  (let [login-link (-> input ::login-link)
        user-email (-> login-link :login-link/email)
        user-db (-> input :user-db/user-db)
        maybe-user (first (user-db/find-by-email! user-db user-email))
        user (if maybe-user maybe-user (user/new! user-email))]
    (assoc input ::user user)))

(defn- assoc-user-session [input]
  (let [user (-> input ::user)
        user-session-id (-> input :user-session/id)
        user-id (:user/id user)
        user-session {:user-session/id user-session-id
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
      assoc-user-session
      put-user!
      put-user-session!
      put-login-link!))

(defn- view-use-login-link-ok [_request]
  [:div "Clicked login link"])

(defmulti view-use-login-link-err ex->err-type)

(defmethod view-use-login-link-err :use-login-link-err/login-link-not-found [_ex _request] 
  [:div "Login link was not found"])

(defmethod view-use-login-link-err :use-login-link-err/login-link-already-used [_ex _request]
  [:div "Login link has already been used"])

(defmethod view-use-login-link-err :use-login-link-err/login-link-expired [_ex _request]
  [:div "Login link has expired. Please request a new one"])

(defmethod view-use-login-link-err :default [_ex _request]
  [:div "An error occurred"])

(defmethod requests/handle-hx :login/clicked-login-link [request]
  (try 
    (let [input (merge request (:request/route request))]
      (use-login-link! input))
    (requests/html (view-use-login-link-ok request))
    (catch Exception ex
      (requests/html (view-use-login-link-err ex request)))))