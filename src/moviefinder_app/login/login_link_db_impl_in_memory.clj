(ns moviefinder-app.login.login-link-db-impl-in-memory
  (:require [moviefinder-app.login.login-link-db]))

(defrecord LoginLinkDbInMemory [login-links-by-id!]
  moviefinder-app.login.login-link-db/LoginLinkDb

  (find-by-email!
    [_this email]
    (->> @login-links-by-id!
         vals
         (filter #(= email (:login-link/email %)))
         set))

  (put!
    [_this input-login-links]
    (let [input (into {} (map (juxt :login-link/id identity) input-login-links))
          next (merge @login-links-by-id! input)]
      (reset! login-links-by-id! next)
      next)))

(defmethod moviefinder-app.login.login-link-db/->LoginLinkDb :login-link-db/impl-in-memory
  [_]
  (->LoginLinkDbInMemory (atom {})))
