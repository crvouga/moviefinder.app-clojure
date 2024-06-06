(ns moviefinder-app.email.send-email-impl-mock
  (:require [moviefinder-app.email.send-email]))


(defn- conj-email [state email]
  (let [sent-emails (get state ::sent-emails #{})
        sent-emails-new (conj sent-emails email)
        state-new (assoc state ::sent-emails sent-emails-new)]
    state-new))

(defrecord SendEmailImplMock [state!]
  moviefinder-app.email.send-email/SendEmail
  (send-email!
    [_this email]
    (swap! state! conj-email email)
    (println ::send-email! email))

  (get-sent-emails!
   [_this]
   (get @state! ::sent-emails #{})))


(defmethod moviefinder-app.email.send-email/->SendEmail :send-email/impl-mock
  [_this]
  (->SendEmailImplMock (atom {})))