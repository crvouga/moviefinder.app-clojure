(ns moviefinder-app.login.login-with-sms.verify-sms.verify-sms-impl-mock
  (:require [moviefinder-app.login.login-with-sms.verify-sms.verify-sms :as verify-sms]
            [moviefinder-app.error :refer [ex]]))

(defn do-sleep [input]
  (when-let [sleep (-> input :verify-sms-mock/sleep)]
    (Thread/sleep sleep)))


(defrecord VerifySMSMock [input]
  verify-sms/VerifySms
  (send-code!
   [_this phone-number]
   (do-sleep input)
   (println "Sending code" (input :verify-sms-mock/code) " to " phone-number))

  (verify-code!
   [_this phone-number code]
   (do-sleep input)
   (when (-> input :verify-sms-mock/code str (not= (str code)))
     (throw (ex :err/wrong-code {:phone-number phone-number
                                 :code code
                                 :expected-code (input :verify-sms-mock/code)})))
   (println "Code" code "verified for" phone-number)))

(defmethod verify-sms/->VerifySms :verify-sms-impl/mock
  [input]
  (->VerifySMSMock input))
