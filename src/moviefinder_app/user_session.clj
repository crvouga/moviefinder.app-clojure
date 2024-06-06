(ns moviefinder-app.user-session
  (:require [moviefinder-app.requests]
            [moviefinder-app.route]
            [moviefinder-app.user-session.db]
            [moviefinder-app.user-session.db-impl]
            [moviefinder-app.view]))

(def user-session-db
  (moviefinder-app.user-session.db/->UserSessionDb
   {:user-session-db/impl :user-session-db/impl-in-memory}))

(defn assoc-user-session! [request] 
  (let [user-session (->> request
                          :user-session/session-id
                          (moviefinder-app.user-session.db/find-user-id-by-session-id! user-session-db))]
    (-> request
        (assoc :user-session/user-session user-session))))

(defn guard-auth! [request view-logged-out view-logged-in]
  (let [user-session (-> request :user-session/user-session)]
    (if user-session
      (view-logged-in user-session)
      (view-logged-out))))
