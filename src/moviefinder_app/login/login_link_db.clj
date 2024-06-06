(ns moviefinder-app.login.login-link-db)


(defprotocol LoginLinkDb
  (find-by-email! [this email])
  (put! [this login-link]))

(defmulti ->LoginLinkDb :login-link-db/impl)