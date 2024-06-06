(ns moviefinder-app.login.login-link)

(defn uuid-v4! []
  (java.util.UUID/randomUUID))

(defn random! []
  {:login-link/id (uuid-v4!)
   :login-link/email "test@test.com"
   :login-link/created-at-posix (System/currentTimeMillis)})