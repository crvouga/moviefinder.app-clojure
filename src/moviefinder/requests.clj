(ns moviefinder.requests
  (:require [clojure.string]
            [hiccup2.core]
            [moviefinder.route]
            [moviefinder.base64]))

(defn- remove-leading-backslash [uri]
  (if (clojure.string/starts-with? uri "/") (subs uri 1) uri))

(defn ring-request->request [ring-request]
  {:request/route (-> ring-request :uri remove-leading-backslash moviefinder.route/decode)
   :request/hx-request? (boolean (get-in ring-request [:headers "hx-request"]))
   :request/session-id (ring-request :session/key)})

(defmulti route-hx (fn [request] (-> request :request/route :route/name)))

(defn html [view]
  {:response/ok? true
   :response/view view
   :status 200
   :headers {"Content-Type" "text/html"}
   :view view
   :body (str (hiccup2.core/html view))})

(defn html-document [view]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :view view
   :body (str "<!doctype html>" (hiccup2.core/html view))})
