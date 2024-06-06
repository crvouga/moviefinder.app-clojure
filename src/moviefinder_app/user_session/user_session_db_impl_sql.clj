(ns moviefinder-app.user-session.user-session-db-impl-sql
  (:require [moviefinder-app.user-session.user-session-db]))


(defrecord UserSessionDbSql []
  moviefinder-app.user-session.user-session-db/UserSessionDb
  (find-user-id-by-session-id! [_this _session-id]
    (throw (Exception. "Not implemented")))

  (insert! [_this _user-session]
    (throw (Exception. "Not implemented"))))


(defmethod moviefinder-app.user-session.user-session-db/->UserSessionDb :user-session-db/impl-sql [_input]
  (->UserSessionDbSql))