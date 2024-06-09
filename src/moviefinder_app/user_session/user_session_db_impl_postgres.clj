(ns moviefinder-app.user-session.user-session-db-impl-postgres
  (:require [moviefinder-app.user-session.user-session-db :as user-session-db]))


(defrecord UserSessionDbPostgres [input]
  user-session-db/UserSessionDb
  (find-by-session-id! [_this _session-id]
    (throw (Exception. "Not implemented")))

  (put! [_this _user-sessions]
    (throw (Exception. "Not implemented"))))


(defmethod user-session-db/->UserSessionDb :user-session-db-impl/postgres [input]
  (->UserSessionDbPostgres input))