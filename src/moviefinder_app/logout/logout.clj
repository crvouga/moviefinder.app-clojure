(ns moviefinder-app.logout.logout 
  (:require [moviefinder-app.user-session.user-session-db :as user-session-db]))


(defn logout! [input]
  (let [user-session-db (input :user-session-db/user-session-db)
        session-id (:session/id input)]
    (user-session-db/zap-by-session-id! user-session-db session-id)
    input))