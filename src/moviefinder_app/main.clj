(ns moviefinder-app.main
  (:require [moviefinder-app.account]
            [moviefinder-app.counter]
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
            [moviefinder-app.movie.details]
            [moviefinder-app.user-session.user-session :as user-session]
            [moviefinder-app.view :as view]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.reload :refer [wrap-reload]]))

(defmethod handle/handle-hx :default [request]
  (-> request
      (assoc :request/route {:route/name :route/home})
      handle/handle-hx))

(defmethod handle/handle :default [request]
  (-> request
      handle/handle-hx
      :response/view
      view/html-doc
      handle/html-doc))

(defn handle [request]
  (if (:request/hx? request)
    (handle/handle-hx request)
    (handle/handle request)))

(defn log-request [request]
  (println (select-keys request [:request/route :session/id :user/id :request/form-data :request/hx?]))
  request)



(defn handle-ring-request [ring-request]
  (-> ring-request
      handle/ring-request->request
      deps/assoc-deps
      user-session/assoc-user-session!
      log-request
      handle
      handle/response->ring-response))

(defn run-server! [input]
  (-> #'handle-ring-request
      (handle/wrap-session-id)
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
  (-main))
