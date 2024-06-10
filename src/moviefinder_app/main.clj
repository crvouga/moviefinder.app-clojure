(ns moviefinder-app.main
  (:require [moviefinder-app.env :as env]
            [moviefinder-app.account]
            [moviefinder-app.counter]
            [moviefinder-app.home]
            [moviefinder-app.login.send-login-link]
            [moviefinder-app.login.use-login-link]
            [moviefinder-app.movie.details]
            [moviefinder-app.user-session.user-session :as user-session]
            [moviefinder-app.handle :as handle]
            [moviefinder-app.deps :as deps]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [moviefinder-app.view :as view]))

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
  (run-server! {:server/port port})
  (println (str "Server running at " base-url)))
