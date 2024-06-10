(ns moviefinder-app.user.user 
  (:require [clojure.string :as str]))


(defn- short-id! []
  (-> (java.util.UUID/randomUUID) 
       str
       (str/split #"-")
       first))

(defn- random-email! []
  (str "email" (short-id!) "@email.com"))

(defn random! []
  {:user/email (random-email!)
   :user/id (java.util.UUID/randomUUID)})

(defn new! [email]
  {:user/email email
   :user/id (java.util.UUID/randomUUID)})

(comment
  (short-id!)
  (random!))