(ns moviefinder-app.email.send-email)


(defprotocol SendEmail
  (send-email! [this email])
  (get-sent-emails! [this]))


(defmulti ->SendEmail :send-email/impl)