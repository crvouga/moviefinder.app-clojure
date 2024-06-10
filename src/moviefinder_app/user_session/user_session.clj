(ns moviefinder-app.user-session.user-session
  (:require [moviefinder-app.handle]
            [moviefinder-app.route] 
            [moviefinder-app.view]
            [moviefinder-app.user-session.user-session-db :as user-session-db]))

(defn random! []
  {:session/id (str (java.util.UUID/randomUUID))
   :user/id (str (java.util.UUID/randomUUID))
   :user-session/created-at-posix (System/currentTimeMillis)})

(defn assoc-user-session! [input]
  (let [user-session-db (input :user-session-db/user-session-db)
        user-sessions (user-session-db/find-by-session-id! user-session-db (:session/id input))]
    (reduce merge input user-sessions)))