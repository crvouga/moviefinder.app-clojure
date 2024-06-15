(ns moviefinder-app.login.login-with-email.login-link.login-link-db-impl
  (:require [moviefinder-app.login.login-with-email.login-link.login-link-db :as login-link-db]
            [moviefinder-app.login.login-with-email.login-link.login-link-db-impl-in-memory]))

(defn in-memory []
  (login-link-db/->LoginLinkDb
   {:login-link-db/impl :login-link-db-impl/in-memory}))