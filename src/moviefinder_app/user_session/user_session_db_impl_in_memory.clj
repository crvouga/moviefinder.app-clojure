(ns moviefinder-app.user-session.user-session-db-impl-in-memory
  (:require [moviefinder-app.user-session.user-session-db :as user-session-db]))

(defn- assoc-missing-timestamps [user-session]
  (merge {:user-session/created-at-posix (System/currentTimeMillis)}
         user-session))

(defrecord UserSessionDbInMemory [sessions-by-session-id!]
  user-session-db/UserSessionDb
  (find-by-session-id! [_this session-id]
    (->> @sessions-by-session-id!
         vals
         (filter #(= session-id (:session/id %)))
         set))
  
  (find-by-user-id! [_this user-id]
    (->> @sessions-by-session-id!
         vals
         (filter #(= user-id (:user/id %)))
         set))

  (put! [_this user-sessions]
    (let [by-id (->> user-sessions
                     (map assoc-missing-timestamps)
                     (map (juxt :session/id identity))
                     (into {}))
          by-id-new (merge by-id @sessions-by-session-id!)]
      (reset! sessions-by-session-id! by-id-new))))


(defmethod user-session-db/->UserSessionDb :user-session-db-impl/in-memory [_input]
  (->UserSessionDbInMemory (atom {})))