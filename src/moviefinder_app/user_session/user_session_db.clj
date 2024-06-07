(ns moviefinder-app.user-session.user-session-db)

(defprotocol UserSessionDb
  (find-user-id-by-session-id! [this session-id])
  (put! [this user-session]))

(defmulti ->UserSessionDb :user-session-db/impl)