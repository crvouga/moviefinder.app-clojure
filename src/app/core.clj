(ns app.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn html []
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "<h1>Hello, World !!!!!</h1>"})

(defn handler [request]
  (html))

(defn -main []
  (run-jetty (wrap-reload #'handler) {:port 3000 :join? false}))
