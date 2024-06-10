(ns moviefinder-app.login.login-link)

(def twenty-four-hours-in-millis
  (* 24 60 60 1000))

(defn expired-at []
  (+ (System/currentTimeMillis) twenty-four-hours-in-millis))

(defn random-login-link-id! []
  (str "login-link:" (java.util.UUID/randomUUID)))

(defn random! []
  {:login-link/id (random-login-link-id!)
   :login-link/email "test@test.com"
   :login-link/created-at-posix (System/currentTimeMillis)
   :login-link/expired-at-posix (expired-at)})

(defn new! [email]
  {:login-link/id (random-login-link-id!)
   :login-link/email email
   :login-link/created-at-posix (System/currentTimeMillis)
   :login-link/expired-at-posix (expired-at)})

(defn mark-as-used [login-link]
  (assoc login-link :login-link/used-at-posix (System/currentTimeMillis)))

(defn used? [login-link]
  (not (nil? (:login-link/used-at-posix login-link))))

(defn mark-as-expired [login-link]
  (assoc login-link :login-link/expired-at-posix (System/currentTimeMillis)))

(defn expired? [login-link]
  (let [now-posix (System/currentTimeMillis)
        expired-posix (:login-link/expired-at-posix login-link)]
    (and (not (nil? expired-posix))
         (<= expired-posix now-posix))))