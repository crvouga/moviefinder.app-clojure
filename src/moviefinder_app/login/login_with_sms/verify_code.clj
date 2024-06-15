(ns moviefinder-app.login.login-with-sms.verify-code
  (:require [moviefinder-app.handle :as handle]
            [moviefinder-app.error :as error]
            [moviefinder-app.login.login-with-sms.login-with-sms :as login-with-sms]
            [moviefinder-app.login.login-with-sms.verify-sms.verify-sms :as verify-sms]
            [moviefinder-app.route :as route]
            [moviefinder-app.view :as view]
            [moviefinder-app.view.icon :as icon]
            [moviefinder-app.user.user-db :as user-db]
            [moviefinder-app.user.user :as user]
            [moviefinder-app.user-session.user-session-db :as user-session-db]
            [moviefinder-app.user-session.user-session :as user-session]))

(defn view-code-verified [_request]
  [:div.flex.flex-col.w-full
   (view/success {:success/title "Code verified"
                  :success/body "You are now logged in."})
   [:div.pt-6.w-fit
    (view/button {:button/label "Back to app"
                  :button/start (icon/arrow-left)
                  :button/element :a
                  :hx-get (-> {:route/name :route/home} route/encode)
                  :hx-push-url (-> {:route/name :route/home} route/encode)
                  :hx-boost true
                  :href (-> {:route/name :route/home} route/encode)})]])


(defmethod login-with-sms/hx-get :route/verified-code [request]
  (view-code-verified request))

;; 
;; 
;; 
;; 

(defn- assoc-phone-number [request]
  (let [phone-number (-> request :request/route :user/phone-number)]
    (-> request
        (assoc :user/phone-number phone-number))))

(defn- assoc-code [request]
  (let [code (-> request :request/form-data :code)]
    (-> request
        (assoc :verify-sms/code code))))

(defn- verify-code! [request]
  (let [verify-sms (-> request :verify-sms/verify-sms)
        phone-number (-> request :user/phone-number)
        code (-> request :verify-sms/code)]
    (verify-sms/verify-code! verify-sms phone-number code)
    request))

(defn- assoc-user [request]
  (let [user-db (-> request :user-db/user-db)
        phone-number (-> request :user/phone-number)
        maybe-user (first (user-db/find-by-phone-number! user-db phone-number))
        user (if maybe-user maybe-user (user/new! request))]
    (-> request
        (assoc ::user user))))

(defn- assoc-user-session [input]
  (let [user (-> input ::user)
        user-session (user-session/new (merge input user))]
    (assoc input ::user-session user-session)))

(defn- put-user! [input]
  (let [user-db (-> input :user-db/user-db)
        user (-> input ::user)]
    (user-db/put! user-db #{user})
    input))

(defn put-user-session! [input]
  (let [user-session-db (-> input :user-session-db/user-session-db)
        user-session (-> input ::user-session)]
    (user-session-db/put! user-session-db #{user-session})
    input))

(defn- assoc-verify-code-ok-route [request]
  (-> request 
      (assoc :request/route {:route/name :route/login-with-sms
                             :login-with-sms/route :route/verified-code})))

(defn- assoc-verify-code-err-route [request ex]
  (-> request
      (assoc :request/route {:route/name :route/login-with-sms
                             :login-with-sms/route :route/verify-code
                             :user/phone-number (-> request :request/route :user/phone-number)
                             :err/err (error/ex->err ex)})))

(defmethod handle/hx-post :route/clicked-verify-code [request]
  (try
    (-> request
        assoc-phone-number
        assoc-code
        assoc-user
        assoc-user-session
        verify-code!
        put-user!
        put-user-session!
        assoc-verify-code-ok-route
        handle/handle-hx-get-push)
    (catch Exception ex
      (-> request
          (assoc-verify-code-err-route ex)
          handle/handle-hx-get-push))))

(defmethod error/err->msg :err/wrong-code [_]
  "Wrong code entered")

(defmethod error/err->msg :default []
  "An error occurred")

(defn view-verify-code-form [request]
  [:form.flex.flex-col.gap-6.w-full
   {:hx-post (-> request
                 :request/route
                 (assoc :route/name :route/clicked-verify-code)
                 route/encode)
    :hx-swap "innerHTML"
    :hx-target "#app"}
   [:p.text-lg
    (str "Enter the code sent to you at "
         (-> request :request/route :user/phone-number))]
   (view/text-field {:text-field/id "code"
                     :text-field/label "Code"
                     :text-field/name "code"
                     :text-field/type "tel"
                     :text-field/required? true
                     :text-field/autofocus? true})
   (view/button {:button/type "submit"
                 :button/label "Verify code"})
   (when (-> request :request/route :err/err)
     (view/alert {:alert/variant :alert/error
                  :alert/message (-> request :request/route :err/err error/err->msg)}))])



(defmethod login-with-sms/hx-get :route/verify-code [request]
  (view-verify-code-form request))

