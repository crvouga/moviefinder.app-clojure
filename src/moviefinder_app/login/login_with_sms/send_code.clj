(ns moviefinder-app.login.login-with-sms.send-code
  (:require [moviefinder-app.handle :as handle]
            [moviefinder-app.login.login-with-sms.login-with-sms :as login-with-sms]
            [moviefinder-app.login.login-with-sms.verify-sms.verify-sms :as verify-sms]
            [moviefinder-app.route :as route]
            [moviefinder-app.view :as view]))

(defn- assoc-form-data [request]
  (let [phone-number (-> request :request/form-data :phone-number)]
    (-> request
        (assoc :user/phone-number phone-number))))

(defn- send-code! [input]
  (let [verify-sms (-> input :verify-sms/verify-sms)
        phone-number (-> input :user/phone-number)]
    (verify-sms/send-code! verify-sms phone-number)
    input))

(defn assoc-verify-code-route [request]
  (-> request
      (assoc :request/route {:route/name :route/login-with-sms
                             :login-with-sms/route :route/verify-code
                             :user/phone-number (-> request :user/phone-number)})))

(defmethod handle/hx-post :route/clicked-send-code [request]
  (-> request
      assoc-form-data
      send-code!
      assoc-verify-code-route
      (handle/html login-with-sms/view)
      handle/hx-push-request-route))

(defn view-send-code-form [request]
  [:form.flex.flex-col.gap-6.w-full
   {:method "POST"
    :hx-post (-> request
                 :request/route
                 (assoc :route/name :route/clicked-send-code)
                 route/encode)
    :hx-swap "outerHTML"
    :hx-target "this"}
   (view/text-field {:text-field/id "phone-number"
                     :text-field/label "Phone number"
                     :text-field/type "tel"
                     :text-field/name "phone-number"
                     :text-field/required? true
                     :text-field/autofocus? true})
   (view/button {:button/type "submit"
                 :button/label "Send code"
                 :button/hx-indicator-id "send-code-indicator"})])

(defmethod login-with-sms/view :default [request]
  (view-send-code-form request))
