(ns moviefinder-app.login.login-with-sms.send-code
  (:require [moviefinder-app.handle :as handle]
            [moviefinder-app.login.login-with-sms.login-with-sms :as login-with-sms]
            [moviefinder-app.login.login-with-sms.verify-code :as verify-code]
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

(defn- assoc-phone-number-in-route [request]
  (let [phone-number (-> request :user/phone-number)]
    (-> request
        (assoc-in [:request/route :user/phone-number] phone-number))))

(defmethod handle/handle-hx :route/clicked-send-code [request]
  (-> request
      assoc-form-data
      send-code!
      assoc-phone-number-in-route
      verify-code/view-verify-code-form
      handle/html))

(defn view-send-code-form [request]
  [:form.flex.flex-col.gap-6.w-full
   {:method "POST"
    :hx-post (-> request
                 :request/route
                 (assoc :route/name :route/clicked-send-code)
                 route/encode)
    :hx-push-url (-> request
                     :request/route
                     (assoc :route/name :route/login-with-sms)
                     (assoc :login-with-sms/step :login-with-sms-step/verify-code)
                     route/encode)
    :hx-swap "outerHTML"
    :hx-target "this"
    :hx-indicator "#send-code-indicator"
    :hx-trigger "submit"}
   (view/text-field {:text-field/id "phone-number"
                     :text-field/label "Phone number"
                     :text-field/type "tel"
                     :text-field/name "phone-number"
                     :text-field/required? true})
   (view/button {:button/type "submit"
                 :button/label "Send code"
                 :button/hx-indicator-id "send-code-indicator"})])

(defmethod login-with-sms/view-step :default [request]
  (view-send-code-form request))
