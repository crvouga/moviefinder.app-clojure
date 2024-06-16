(ns moviefinder-app.main
  (:require [moviefinder-app.account]
            [moviefinder-app.db-migration :as db-migration]
            [moviefinder-app.deps :as deps]
            [moviefinder-app.env :as env]
            [moviefinder-app.handle :as handle]
            [moviefinder-app.home]
            [moviefinder-app.login.login-with-email.send-login-link]
            [moviefinder-app.login.login-with-email.use-login-link]
            [moviefinder-app.login.login-with-sms.login-with-sms]
            [moviefinder-app.login.login-with-sms.send-code]
            [moviefinder-app.login.login-with-sms.verify-code]
            [moviefinder-app.logout.logout]
            [moviefinder-app.media.media-details]
            [moviefinder-app.session :as session]
            [moviefinder-app.user-session.user-session :as user-session]
            [moviefinder-app.view :as view]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.reload :refer [wrap-reload]]))

(defmethod handle/hx-get :default [request]
  (-> request
      (assoc :request/route {:route/name :route/home})
      handle/hx-get))

(defmethod handle/handle :default [request]
  (-> request
      (handle/html (comp view/html-doc :response/view handle/hx-get))))


(defn log-request [request]
  (println (select-keys request [:request/route :request/method :session/id :user/id :request/form-data :request/hx?]))
  request)



(defn handle-ring-request [ring-request]
  (-> ring-request
      handle/ring-request->request
      deps/assoc-deps
      user-session/assoc-user-session!
      log-request
      handle/root-handle
      handle/response->ring-response))

;; 
;; 
;; 
;; 
;; 
;; 
;; 

(defn set-cookie-value [key value]
  (str key "=" value
       "; Path=/"
       "; HttpOnly"
       "; SameSite=Strict"
       #_"; Secure"
       "; Max-Age=31536000"))

(defn assoc-set-cookie [ring-response key value]
  (assoc-in ring-response [:headers "Set-Cookie"] (set-cookie-value key value)))

(defn get-cookie [ring-request key]
  (get-in ring-request [:cookies key :value]))

(defn wrap-session-id [handler]
  (fn [ring-request]
    (let [session-id (get-cookie ring-request "session-id")
          session-id-final (or session-id (session/random-session-id!))
          ring-request (assoc ring-request :session/id session-id-final)
          ring-response (handler ring-request)
          ring-response-final (if session-id
                                ring-response
                                (assoc-set-cookie ring-response "session-id" session-id-final))]
      ring-response-final)))


(defn run-server! [input]
  (-> #'handle-ring-request
      (wrap-session-id)
      (wrap-cookies)
      (wrap-params)
      (wrap-reload)
      (run-jetty {:port (input :server/port) :join? false})))

(def port (-> (moviefinder-app.env/get! "PORT") Integer/parseInt))
(def base-url (moviefinder-app.env/get! "BASE_URL"))

(defn -main []
  (println "Running migrations...")
  (db-migration/db-up!)
  (println "Migrations done.")
  (println "Starting server...")
  (run-server! {:server/port port})
  (println (str "Server started."))
  (println (str "Visit " base-url " in your browser.")))

(comment
  (-main)
  )
