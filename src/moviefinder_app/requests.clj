(ns moviefinder-app.requests
  (:require [clojure.string]
            [hiccup2.core]
            [moviefinder-app.base64]
            [moviefinder-app.route]))


;; 
;; 
;; 
;; Routing
;; 
;; 
;; 

(def request-route (comp :route/name :request/route))

(defmulti handle-hx request-route)

(defmulti handle request-route)

;; 
;; 
;; 
;; Request
;; 
;; 
;; 

(defn- remove-leading-backslash [uri]
  (if (clojure.string/starts-with? uri "/") (subs uri 1) uri))

(defn- route [ring-request]
  (-> ring-request :uri remove-leading-backslash moviefinder-app.route/decode))

(defn- hx-request? [ring-request]
  (boolean (get-in ring-request [:headers "hx-request"])))

(defn- session-id [ring-request]
  (ring-request :session/key))

(defn ring-request->request [ring-request]
  {:request/route (route ring-request)
   :request/hx-request? (hx-request? ring-request)
   :request/session-id (session-id ring-request)})

;; 
;; 
;; 
;; Response
;; 
;; 
;; 

(defmulti response->ring-response :response/type)

(defn- status [response]
  (if (:response/ok? response) 200 500))

(defn- html-body [response]
  (-> response :response/view hiccup2.core/html str))

(def html-headers
  {"Content-Type" "text/html"})

(defmethod response->ring-response :response-type/html [response]
  {:status (status response)
   :headers html-headers
   :body (html-body response)})

(defn- append-doc-type [html]
  (str "<!doctype html>" html))

(def html-document-body (comp append-doc-type html-body))

(defmethod response->ring-response :response-type/html-document [response]
  {:status (status response)
   :headers html-headers
   :body (html-document-body response)})

(defmethod response->ring-response :default [_response]
  {:status 500
   :headers {}
   :body "Internal Server Error"})

(defn html [view]
  {:response/ok? true
   :response/view view
   :response/type :response-type/html})

(defn html-document [view]
  {:response/ok? true
   :response/view view
   :response/type :response-type/html-document})
