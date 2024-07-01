(ns moviefinder-app.user-session.user-session
  (:require [moviefinder-app.handle]
            [moviefinder-app.route] 
            [moviefinder-app.view]
            [moviefinder-app.user-session.user-session-db :as user-session-db]
            [moviefinder-app.session :as session]
            [moviefinder-app.user.user :as user]))

(defn random-user-session-id! []
  (str "user-session:" (session/random-session-id!)))

(defn random! []
  {:session/id (session/random-session-id!)
   :user/id (user/random-user-id!)
   :user-session/session-id (session/random-session-id!)
   :user-session/user-id (user/random-user-id!)
   :user-session/id (random-user-session-id!)
   :user-session/created-at-posix (System/currentTimeMillis)})

(defn new [input]
  (-> input
      (select-keys [:user/id :session/id])))

(defn assoc-user-session! [input]
  (let [user-session-db (input :user-session-db/user-session-db)
        user-sessions (user-session-db/find-by-session-id! user-session-db (:session/id input))]
    (reduce merge input user-sessions)))