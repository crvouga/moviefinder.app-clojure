(ns moviefinder.app.user-session.db-impl-sql
  (:require [moviefinder.app.user-session.db :refer [UserSessionDb ->UserSessionDb]]))


(defrecord UserSessionDbSql []
  UserSessionDb
  (find-user-id-by-session-id! [_this _session-id]
    (throw (Exception. "Not implemented")))

  (insert! [_this _user-session]
    (throw (Exception. "Not implemented"))))


(defmethod ->UserSessionDb :user-session-db/impl-sql [_input]
  (->UserSessionDbSql))