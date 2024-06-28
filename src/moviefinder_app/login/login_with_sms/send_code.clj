(ns moviefinder-app.login.login-with-sms.send-code
  (:require [moviefinder-app.handle :as handle]
            [moviefinder-app.login.login-with-sms.login-with-sms :as login-with-sms]
            [moviefinder-app.login.login-with-sms.verify-sms.verify-sms :as verify-sms]
            [moviefinder-app.route :as route]
            [moviefinder-app.phone-number :as phone-number]
            [moviefinder-app.view :as view]
            [moviefinder-app.error :as error]))

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

(defn assoc-send-code-err-route [request ex]
  (-> request
      (assoc :request/route {:route/name :route/login-with-sms
                             :user/phone-number (-> request :request/route :user/phone-number)
                             :err/err (error/ex->err ex)})))

(defn assoc-valid-phone-number [input]
  (-> input
      (update :user/phone-number phone-number/normalize)))

(defmethod handle/hx-post :route/clicked-send-code [request]
  (try
    (-> request
        assoc-form-data
        assoc-valid-phone-number
        send-code!
        assoc-verify-code-route
        (handle/html login-with-sms/view)
        handle/hx-push-request-route)
    (catch Exception ex
      (-> request
          (assoc-send-code-err-route ex)
          (handle/html login-with-sms/view)
          handle/hx-push-request-route))))


(defmethod error/err->msg :default [_err]
  (str "An error occurred"))

(defmethod error/err->msg :err/verify-sms-errored [_err]
  (str "An error occurred while verifying the code"))

(defmethod error/err->msg :err/send-sms-errored [_err]
  (str "An error occurred while sending the code"))
  

(defn view-send-code-form [request]
  [:form.flex.flex-col.gap-6.w-full
   {:hx-post (-> request
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
                 :button/label "Send code"})
   (when (-> request :request/route :err/err)
     (view/alert {:alert/variant :alert/error
                  :alert/message (-> request :request/route :err/err error/err->msg)}))])

(defmethod login-with-sms/view :default [request]
  (view-send-code-form request))
