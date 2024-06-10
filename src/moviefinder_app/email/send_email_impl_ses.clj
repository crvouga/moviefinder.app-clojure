(ns moviefinder-app.email.send-email-impl-ses
  (:require [moviefinder-app.email.send-email :as send-email]
            [moviefinder-app.email.email :as email]))



(defrecord SendEmailImplSes []
  send-email/SendEmail
  (send-email! [_this _email]
    (println "SendEmailImplSes.send-email! not implemented")
    (println (-> _email (email/assoc-body-html) :email/body-html))
    #_(throw (Exception. "SendEmailImplSes.send-email! not implemented"))))

(defmethod send-email/->SendEmail :send-email/impl-ses
  [_this]
  (->SendEmailImplSes))


(comment
  (def send-email (->SendEmailImplSes))

  (def email 
    {:email/to "crvouga@gmail.com"
     :email/subject "Test"
     :email/body-view [:h1 "Test"]})
  
  
  (send-email/send-email! send-email email)
  
  
  )