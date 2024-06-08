(ns moviefinder-app.login.login-link-db-impl-in-memory
  (:require [moviefinder-app.login.login-link-db :as login-link-db]))

(defn index-by [f m]
  (->> m
       (map (juxt f identity))
       (into {})))

(defrecord LoginLinkDbInMemory [login-links-by-id!]
  login-link-db/LoginLinkDb

  (find-by-email!
    [_this email]
    (->> @login-links-by-id!
         vals
         (filter #(= email (:login-link/email %)))
         set))

  (put!
    [_this input-login-links]
    (let [input (index-by :login-link/id input-login-links)
          next (merge @login-links-by-id! input)]
      (reset! login-links-by-id! next)
      next))
  
  (find-by-id!
    [_this id]
    (->> @login-links-by-id!
         vals
         (filter #(= id (:login-link/id %)))
         set)))

(defmethod login-link-db/->LoginLinkDb :login-link-db-impl/in-memory
  [_]
  (->LoginLinkDbInMemory (atom {})))
