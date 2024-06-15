(ns moviefinder-app.user.user-db-impl
  (:require [moviefinder-app.user.user-db-impl-in-memory]
            [moviefinder-app.user.user-db :as user-db]))


(defn in-memory []
  (user-db/->UserDb
   {:user-db/impl :user-db-impl/in-memory}))