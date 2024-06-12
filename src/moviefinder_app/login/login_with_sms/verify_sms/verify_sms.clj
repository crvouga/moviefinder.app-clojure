(ns moviefinder-app.login.login-with-sms.verify-sms.verify-sms)

(defprotocol VerifySms
  (send-code! [this phone-number])
  (verify-code! [this phone-number code]))


(defmulti ->VerifySms :verify-sms/impl)