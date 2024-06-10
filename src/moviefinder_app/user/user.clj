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

(defn random! []
  {:user/email (random-email!)
   :user/id (random-user-id!)})

(defn new! [email]
  {:user/email email
   :user/id (random-user-id!)})

(comment
  (short-id!)
  (random!))