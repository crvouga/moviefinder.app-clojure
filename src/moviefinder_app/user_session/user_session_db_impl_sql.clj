(ns moviefinder-app.user-session.user-session-db-impl-sql
  (:require [moviefinder-app.user-session.user-session-db :as user-session-db]))


(defrecord UserSessionDbSql []
  user-session-db/UserSessionDb
  (find-by-session-id! [_this _session-id]
    (throw (Exception. "Not implemented")))

  (put! [_this _user-sessions]
    (throw (Exception. "Not implemented"))))


(defmethod user-session-db/->UserSessionDb :user-session-db/impl-sql [_input]
  (->UserSessionDbSql))