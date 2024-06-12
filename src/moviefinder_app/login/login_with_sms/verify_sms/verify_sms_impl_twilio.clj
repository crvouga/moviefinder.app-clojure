(ns moviefinder-app.login.login-with-sms.verify-sms.verify-sms-impl-twilio
  (:require [moviefinder-app.login.login-with-sms.verify-sms.verify-sms :as verify-sms]))


(defrecord VerifySMSTwilio []
  verify-sms/VerifySms
  (send-code! [_this phone-number]
    (println "Sending code to" phone-number))

  (verify-code! [_this phone-number code]
    (println "Verifying code" code "for" phone-number)))

(defmethod verify-sms/->VerifySms :verify-sms/impl-twilio
  [_]
  (->VerifySMSTwilio))
