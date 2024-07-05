(ns moviefinder-app.http-client
  (:require [clj-http.client :as client]))


(defn request [params]
  (println "http-client/request: " (select-keys params [:method :url :query-params :headers]))
  (client/request params))

(def cache! (atom {}))

(defn request-with-cache [params]
  (println "http-client/request-with-cache: " (select-keys params [:method :url :query-params :headers]))
  (let [cached (get @cache! params)]
    (if cached
      cached
      (let [response (request params)]
        (swap! cache! assoc params response)
        response))))