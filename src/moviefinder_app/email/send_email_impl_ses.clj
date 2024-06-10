(ns moviefinder-app.email.send-email-impl-ses
  (:require [moviefinder-app.email.send-email]))


(defrecord SendEmailImplSes []
  moviefinder-app.email.send-email/SendEmail
  (send-email! [_this _email]
    (throw (Exception. "SendEmailImplSes.send-email! not implemented"))))

(defmethod moviefinder-app.email.send-email/->SendEmail :send-email/impl-ses
  [_this]
  (->SendEmailImplSes))