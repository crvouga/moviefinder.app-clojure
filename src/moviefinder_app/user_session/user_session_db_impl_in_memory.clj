(ns moviefinder-app.user-session.user-session-db-impl-in-memory
  (:require [moviefinder-app.user-session.user-session-db :refer [->UserSessionDb
                                                                  UserSessionDb]]))



(defrecord UserSessionDbInMemory [sessions-by-session-id!]
  UserSessionDb
  (find-user-id-by-session-id! [_this _session-id]
    (when-let [session (get @sessions-by-session-id! _session-id)]
      (:user/id session)))

  (insert! [_this user-session]
    (swap! sessions-by-session-id! assoc (:user-session/id user-session) user-session)))


(defmethod ->UserSessionDb :user-session-db/impl-in-memory [_input]
  (->UserSessionDbInMemory (atom {})))