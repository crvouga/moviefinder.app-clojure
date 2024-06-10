(ns moviefinder-app.user-session.user-session-db)

(defprotocol UserSessionDb
  (find-by-session-id! [this session-id])
  (find-by-user-id! [this user-id])
  (put! [this user-sessions]))

(defmulti ->UserSessionDb :user-session-db/impl)