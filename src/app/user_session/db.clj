(ns app.user-session.db)


(defprotocol UserSessionDb
  (find-user-id-by-session-id! [this session-id])
  (insert! [this user-session]))