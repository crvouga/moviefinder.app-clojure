(ns app.res
  (:require [clojure.string]
            [hiccup2.core]
            [app.base64]))

(defn encode-route [route]
  (-> route pr-str app.base64/encode))

(defn decode-route [route]
  (try
    (-> route app.base64/decode read-string)
    (catch Exception _
      nil)))

(defn- remove-leading-backslash [uri]
  (if (clojure.string/starts-with? uri "/") (subs uri 1) uri))

(defn ring-request->request [ring-request]
  {:request/route (-> ring-request :uri remove-leading-backslash decode-route)
   :request/hx-request? (boolean (get-in ring-request [:headers "hx-request"]))})

(defmulti handle (fn [request] (-> request :request/route :route/name)))

(defn html [view]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :view view
   :body (str (hiccup2.core/html view))})

(defn html-document [view]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :view view
   :body (str "<!doctype html>" (hiccup2.core/html view))})
