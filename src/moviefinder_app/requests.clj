(ns moviefinder-app.requests
  (:require [clojure.string :as string]
            [hiccup2.core :as hiccup]
            [ring.util.response :as ring-response]
            [moviefinder-app.route :as route]))


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
  (if (string/starts-with? uri "/") (subs uri 1) uri))

(defn- route [ring-request]
  (-> ring-request :uri remove-leading-backslash route/decode))

(defn- hx? [ring-request]
  (boolean (get-in ring-request [:headers "hx-request"])))


(defn- ring-session-id [ring-request]
  (get-in ring-request [:session/key]))


(defn- valid-keyword? [s]
  (try
    (boolean (keyword s))
    (catch Exception _ 
      false)))

(defn- str->keyword [k]
  (if (and (string? k) (valid-keyword? k))
    (keyword k)
    k))

(defn- str-keys->keywords [m]
  (update-keys m str->keyword))

(defn assoc-route [request ring-request]
  (assoc request :request/route (route ring-request)))

(defn assoc-hx? [request ring-request]
  (let [hx? (hx? ring-request)]
    (if hx?
      (assoc request :request/hx? hx?)
      request)))

(defn assoc-user-session-id [request ring-request]
  (let [user-session-id (ring-session-id ring-request)]
    (println "user-session-id="user-session-id)
    (if user-session-id
      (assoc request :user-session/id user-session-id)
      request)))


(defn assoc-form-data [request ring-request]
  (let [form-data (-> ring-request :form-params str-keys->keywords)]
    (if-not (empty? form-data)
      (assoc request :request/form-data form-data)
      request)))

(defn ring-request->request [ring-request]
  (-> {}
      (assoc-route ring-request)
      (assoc-hx? ring-request)
      (assoc-user-session-id ring-request)
      (assoc-form-data ring-request)))

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
  (-> response
      :response/view
      hiccup/html
      str))

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

(defn html-doc [view]
  {:response/ok? true
   :response/view view
   :response/type :response-type/html-document})


;; 
;; 
;; 
;; 
;; 
;; 

(defn redirect [route]
  {:response/ok? true
   :response/route route
   :response/type :response-type/redirect})

(defn response->location [response]
  (-> response :response/route route/encode str))

(defmethod response->ring-response :response-type/redirect [response]
  (-> response
      response->location
      ring-response/redirect))
