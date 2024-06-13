(ns moviefinder-app.user.user-db-impl-in-memory
  (:require [moviefinder-app.user.user-db :as user-db]))


(defrecord UserDbImplInMemory [users-by-id!]
  user-db/UserDb
  (find-by-email!
   [_this email]
   (->> @users-by-id!
        vals
        (filter #(= email (:user/email %)))
        set))

  (find-by-id!
   [_this id]
   (->> @users-by-id!
        vals
        (filter #(= id (:user/id %)))
        set))

  (find-by-phone-number!
   [_this phone-number]
   (->> @users-by-id!
        vals
        (filter #(= phone-number (:user/phone-number %)))
        set))

  (put!
   [_this users]
   (let [users-by-id (->> users (map (juxt :user/id identity)) (into {}))
         users-by-id-new (merge @users-by-id! users-by-id)]
     (reset! users-by-id! users-by-id-new))))

(defmethod user-db/->UserDb :user-db-impl/in-memory
  [_]
  (->UserDbImplInMemory
   (atom {})))