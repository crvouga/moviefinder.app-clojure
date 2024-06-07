(ns moviefinder-app.user.user)


(defn random! []
  {:user/email "test@test.com"
   :user/id (java.util.UUID/randomUUID)})

(defn new! [email]
  {:user/email email
   :user/id (java.util.UUID/randomUUID)})