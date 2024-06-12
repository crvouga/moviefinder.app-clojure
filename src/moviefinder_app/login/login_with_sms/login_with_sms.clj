(ns moviefinder-app.login.login-with-sms.login-with-sms
  (:require [moviefinder-app.handle :as handle]
            [moviefinder-app.view :as view]
            [moviefinder-app.login.login-with-sms.verify-sms.verify-sms :as verify-sms]
            [moviefinder-app.route :as route]))

(defn- view-layout [& children]
  (view/app-tabs-layout
   {:route/name :route/account}
   [:div.w-full.flex.flex-col
    (view/top-bar {:top-bar/title "Login with phone"})
    [:div.w-full.flex-1.p-6
     children]]))

(defn view-verify-code-ok [_request]
  [:div "Code verified"])

(defmethod handle/handle-hx ::clicked-verify-code [request]
  (let [verify-sms (-> request :verify-sms/verify-sms)
        phone-number (-> request :request/form-data :phone-number)
        code (-> request :request/form-data :code)]
    (verify-sms/verify-code! verify-sms phone-number code))
  (handle/html (view-verify-code-ok request)))

(defn view-verify-code-form [_request]
  [:form.flex.flex-col.gap-6.w-full
   {:method "POST"
    :hx-post (-> {:route/name ::clicked-verify-code} route/encode)
    :hx-push-url (-> {:route/name :route/login-with-sms ::step ::step-verified-code} route/encode)
    :hx-swap "outerHTML"
    :hx-target "this"
    :hx-indicator "#verify-code-indicator"}
   #_(view/text-field {:text-field/id "phone-number"
                       :text-field/label "Phone number"
                       :text-field/type "tel"
                       :text-field/initial-value (-> _request :request/form-data :phone-number)
                       :text-field/hidden? true})
   (view/text-field {:text-field/id "code"
                     :text-field/label "Code"
                     :text-field/type "tel"})
   (view/button {:button/type "submit"
                 :button/label "Verify code"})])

(defmethod handle/handle-hx ::clicked-send-code [request]
  (let [verify-sms (-> request :verify-sms/verify-sms)
        phone-number (-> request :request/form-data :phone-number)]
    (verify-sms/send-code! verify-sms phone-number))
  (handle/html (view-verify-code-form request)))

(defn view-send-code-form [_request]
  [:form.flex.flex-col.gap-6.w-full
   {:method "POST"
    :hx-post (-> {:route/name ::clicked-send-code} route/encode)
    :hx-push-url (-> {:route/name :route/login-with-sms ::step ::step-sent-code} route/encode)
    :hx-swap "outerHTML"
    :hx-target "this"
    :hx-indicator "#send-code-indicator"}
   (view/text-field {:text-field/id "phone-number"
                     :text-field/label "Phone number"
                     :text-field/type "tel"})
   (view/button {:button/type "submit"
                 :button/label "Send code"
                 :button/hx-indicator-id "send-code-indicator"})])

(defmulti view-step (comp ::step :request/route))

(defmethod view-step ::step-sent-code [request]
  (view-verify-code-form request))

(defmethod view-step ::step-verified-code [request]
  (view-verify-code-ok request))

(defmethod view-step :default [request]
  (view-send-code-form request))

(defn view-login-with-sms [request]
  (view-layout
   (view-step request)))

(defmethod handle/handle-hx :route/login-with-sms [request]
  (handle/html (view-login-with-sms request)))