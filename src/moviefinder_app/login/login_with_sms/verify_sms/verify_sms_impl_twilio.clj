(ns moviefinder-app.login.login-with-sms.verify-sms.verify-sms-impl-twilio
  (:require [clj-http.client :as http]
            [moviefinder-app.base64 :as base64]
            [moviefinder-app.env :as env]
            [moviefinder-app.login.login-with-sms.verify-sms.verify-sms :as verify-sms]
            [moviefinder-app.error :as error]))

(def twilio-service-sid (env/get! "TWILIO_SERVICE_SID"))
(def twilio-auth-token (env/get! "TWILIO_AUTH_TOKEN"))
(def twilio-account-sid (env/get! "TWILIO_ACCOUNT_SID"))

(def authorization (str "Basic " (base64/encode (str twilio-account-sid ":" twilio-auth-token))))

(def base-url (str "https://verify.twilio.com/v2/Services/" twilio-service-sid))
(def verifications-url (str base-url "/Verifications"))
(def verification-checks-url (str base-url "/VerificationCheck"))

(defn post-send-code! [phone-number]
  (http/post verifications-url
             {:form-params {:To phone-number :Channel "sms"}
              :headers {"Authorization" authorization
                        "Content-Type" "application/x-www-form-urlencoded"}}))



(defn post-verify-code! [phone-number code]
  (http/post verification-checks-url
             {:form-params {:To phone-number :Code code}
              :headers {"Authorization" authorization
                        "Content-Type" "application/x-www-form-urlencoded"}}))

(defrecord VerifySMSTwilio []
  verify-sms/VerifySms
  (send-code! [_this phone-number]
    (try
      (println "Sending code to" phone-number)
      (post-send-code! phone-number)
      (println "Sent code to" phone-number "successfully")
      (catch Exception ex
        (println "Errored while sending code:" (-> ex .getMessage))
        (throw (error/ex :err/send-sms-errored {:user/phone-number phone-number})))))

  (verify-code! [_this phone-number code]
    (try
      (post-verify-code! phone-number code)
      (println "Verified code for " phone-number " successfully")
      (catch Exception ex
        (println "Errored while verifying code:" (-> ex .getMessage))
        (throw (error/ex :err/verify-sms-errored {:user/phone-number phone-number}))))))

(defmethod verify-sms/->VerifySms :verify-sms-impl/twilio
  [_]
  (->VerifySMSTwilio))

(comment
  (def verify-sms (verify-sms/->VerifySms {:verify-sms/impl :verify-sms-impl/twilio}))
  (verify-sms/send-code! verify-sms "4802098698")
  (verify-sms/verify-code! verify-sms "+14802098698" "123"))
