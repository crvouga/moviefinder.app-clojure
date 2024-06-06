(ns moviefinder-app.login.login-link)

(defn uuid-v4! []
  (java.util.UUID/randomUUID))

(defn random! []
  {:login-link/id (uuid-v4!)
   :login-link/email "test@test.com"
   :login-link/created-at-posix (System/currentTimeMillis)})

(defn new! [email]
  {:login-link/id (uuid-v4!)
   :login-link/email email
   :login-link/created-at-posix (System/currentTimeMillis)})

(defn mark-as-used [login-link]
  (assoc login-link :login-link/used-at-posix (System/currentTimeMillis)))