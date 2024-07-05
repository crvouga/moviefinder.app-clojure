(ns moviefinder-app.http-client
  (:require [clj-http.client :as client]))


(defn request [params]
  (println "http-client/request: " (select-keys params [:method :url :query-params :headers]))
  (client/request params))
