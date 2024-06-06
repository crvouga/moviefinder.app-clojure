(ns moviefinder-app.login.login-link-db)


(defprotocol LoginLinkDb
  (find-by-email! [this email])
  (find-by-id! [this id])
  (put! [this login-link]))

(defmulti ->LoginLinkDb :login-link-db/impl)