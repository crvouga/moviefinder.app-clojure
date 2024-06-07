(ns moviefinder-app.user-session.user-session-db-impl-in-memory
  (:require [moviefinder-app.user-session.user-session-db :refer [->UserSessionDb
                                                                  UserSessionDb]]))



(defrecord UserSessionDbInMemory [sessions-by-session-id!]
  UserSessionDb
  (find-by-session-id! [_this session-id]
    (->> @sessions-by-session-id!
         vals
         (filter #(= session-id (:user-session/id %)))
         set))

  (put! [_this user-sessions]
    (let [by-id (->> user-sessions (map (juxt :user-session/id identity)) (into {}))
          by-id-new (merge by-id @sessions-by-session-id!)]
      (reset! sessions-by-session-id! by-id-new))))


(defmethod ->UserSessionDb :user-session-db/impl-in-memory [_input]
  (->UserSessionDbInMemory (atom {})))