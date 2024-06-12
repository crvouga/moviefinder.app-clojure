(ns moviefinder-app.login.login-with-sms.login-with-sms 
  (:require [moviefinder-app.handle :as handle]
            [moviefinder-app.view :as view]
            [moviefinder-app.login.login-with-sms.verify-sms.verify-sms :as verify-sms]))

(defn view-verify-code-ok [_request]
  [:div "Code verified"])

(defmethod handle/handle ::clicked-verify-code [request]
  (let [verify-sms (-> request :verify-code/:verify-sms)
        phone-number (-> request :request/form-data :phone-number)
        code (-> request :request/form-data :code)]
    (verify-sms/verify-code! verify-sms phone-number code))
  (handle/html (view-verify-code-ok request)))

(defn view-verify-code [_request]
  [:form
   (view/text-field {:text-field/id "phone-number"
                     :text-field/label "Phone number"
                     :text-field/type "tel"
                     :text-field/initial-value (-> _request :request/form-data :phone-number)
                     :text-field/hidden? true})
   (view/text-field {:text-field/id "code"
                     :text-field/label "Code"
                     :text-field/type "tel"})
   (view/button {:button/type "submit"
                 :button/text "Verify code"})])

(defmethod handle/handle ::clicked-send-code [request]
  (let [verify-sms (-> request :verify-code/:verify-sms)
        phone-number (-> request :request/form-data :phone-number)]
    (verify-sms/send-code! verify-sms phone-number))
  
  (handle/html (view-verify-code request)))

(defn view-send-code [_request]
  [:form
   (view/text-field {:text-field/id "phone-number"
                     :text-field/label "Phone number"
                     :text-field/type "tel"})
   (view/button {:button/type "submit"
                 :button/text "Send code"})])

(defmethod handle/handle-hx :route/login-with-sms [request]
  (handle/html (view-send-code request)))