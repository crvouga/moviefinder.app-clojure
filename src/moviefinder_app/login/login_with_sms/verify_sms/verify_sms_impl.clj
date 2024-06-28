(ns moviefinder-app.login.login-with-sms.verify-sms.verify-sms-impl
  (:require [moviefinder-app.login.login-with-sms.verify-sms.verify-sms :as verify-sms]
            [moviefinder-app.login.login-with-sms.verify-sms.verify-sms-impl-mock]
            [moviefinder-app.login.login-with-sms.verify-sms.verify-sms-impl-twilio]))

(defn mock []
  (verify-sms/->VerifySms
   {:verify-sms/impl :verify-sms-impl/mock
    :verify-sms-mock/code 123
    :verify-sms-mock/sleep 3000}))

(defn twilio []
  (verify-sms/->VerifySms
   {:verify-sms/impl :verify-sms-impl/twilio}))