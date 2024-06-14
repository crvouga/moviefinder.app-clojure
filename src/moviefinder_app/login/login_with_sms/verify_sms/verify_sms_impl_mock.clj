(ns moviefinder-app.login.login-with-sms.verify-sms.verify-sms-impl-mock
  (:require [moviefinder-app.login.login-with-sms.verify-sms.verify-sms :as verify-sms]
            [moviefinder-app.error :refer [err]]))


(defrecord VerifySMSMock [input]
  verify-sms/VerifySms
  (send-code!
   [_this phone-number]
   (println "Sending code" (input :verify-sms-mock/code) " to " phone-number))

  (verify-code!
   [_this phone-number code]
   (when (-> input :verify-sms-mock/code str (not= (str code)))
     (throw (err :err/wrong-code {:phone-number phone-number
                                  :code code
                                  :expected-code (input :verify-sms-mock/code)})))
   (println "Code" code "verified for" phone-number)))

(defmethod verify-sms/->VerifySms :verify-sms-impl/mock
  [input]
  (->VerifySMSMock input))
