(ns moviefinder-app.user-session.user-session-db-interface)

(defprotocol UserSessionDb
  (find-by-session-id! [this session-id])
  (put! [this user-sessions]))

(defmulti ->UserSessionDb :user-session-db/impl)