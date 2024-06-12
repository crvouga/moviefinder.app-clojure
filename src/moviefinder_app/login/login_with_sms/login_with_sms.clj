(ns moviefinder-app.login.login-with-sms.login-with-sms 
  (:require [moviefinder-app.handle :as handle]
            [moviefinder-app.view :as view]))



(defn view-login-with-sms [_request]
  [:form
   (view/text-field {:text-field/id "phone-number"
                     :text-field/label "Phone number"
                     :text-field/type "tel"})
   (view/button {:button/type "submit"
                 :button/text "Send code"})])


(defmethod handle/handle :route/login-with-sms [request]
  (view-login-with-sms request))