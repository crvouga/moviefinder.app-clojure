(ns moviefinder-app.user.user-db-impl-in-memory
  (:require [moviefinder-app.user.user-db :as user-db]))


(defrecord UserDbImplInMemory [user-by-id!]
  user-db/UserDb
  (find-by-email!
   [_this email]
   (->> @user-by-id!
        vals
        (filter #(= email (:user/email %)))
        set))
  
  (find-by-id!
   [_this id]
   (->> @user-by-id!
        vals
        (filter #(= id (:user/id %)))
        set))
  
  (put!
   [_this users]
   (let [users-by-id (->> users (map (juxt :user/id identity)) (into {}))
         users-by-id-new (merge @user-by-id! users-by-id)]
      (reset! user-by-id! users-by-id-new))))

(defmethod user-db/->UserDb :user-db-impl/in-memory
  [_]
  (->UserDbImplInMemory
   (atom {})))