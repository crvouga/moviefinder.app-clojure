(ns moviefinder-app.main
  (:require [moviefinder-app.env :as env]
            [moviefinder-app.account]
            [moviefinder-app.counter]
            [moviefinder-app.home]
            [moviefinder-app.login.send-login-link]
            [moviefinder-app.login.use-login-link]
            [moviefinder-app.movie.details]
            [moviefinder-app.requests :as requests]
            [moviefinder-app.deps :as deps]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.session.memory :refer [memory-store]]
            [moviefinder-app.view :as view]))

(defmethod requests/handle-hx :default [request]
  (-> request
      (assoc :request/route {:route/name :route/home})
      requests/handle-hx))

(defmethod requests/handle :default [request]
  (-> request
      requests/handle-hx
      :response/view
      view/html-doc
      requests/html-doc))

(defn handle [request]
  (if (:request/hx? request)
    (requests/handle-hx request)
    (requests/handle request)))

(defn tap [x]
  (println x)
  x)

(def deps (deps/deps-real))

(defn assoc-deps [request]
  (merge request deps))

(defn handle-ring-request [ring-request]
  (-> ring-request
      requests/ring-request->request
      tap
      assoc-deps
      handle
      requests/response->ring-response))

(defn run-server! [input]
  (-> #'handle-ring-request
      (wrap-params)
      (wrap-session {:store (memory-store)})
      (wrap-reload)
      (run-jetty {:port (input :server/port) :join? false})))

(def port (-> (moviefinder-app.env/get! "PORT") Integer/parseInt))
(def base-url (moviefinder-app.env/get! "BASE_URL"))
(defn -main []
  (run-server! {:server/port port})
  (println (str "Server running at " base-url)))
