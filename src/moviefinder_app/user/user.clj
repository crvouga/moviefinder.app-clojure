(ns moviefinder-app.user.user 
  (:require [clojure.string :as str]))


(defn- short-id! []
  (-> (java.util.UUID/randomUUID) 
       str
       (str/split #"-")
       first))

(defn random-user-id! []
  (str "user:" (java.util.UUID/randomUUID)))

(defn random-email! []
  (str "email" (short-id!) "@email.com"))

(defn random-phone-number! []
  (str "123-456-7890" (short-id!)))

(defn random! []
  {:user/phone-number (random-phone-number!)
   :user/email (random-email!)
   :user/id (random-user-id!)})

(defn new! [input]
  (-> (select-keys input [:user/email :user/phone-number])
      (assoc :user/id (random-user-id!))))

(comment
  (short-id!)
  (random!))