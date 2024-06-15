(ns moviefinder-app.handle
  (:require [clojure.string :as string]
            [hiccup2.core :as hiccup]
            [moviefinder-app.session :as session]
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

(defmulti handle-hx-get request-route)

(defmulti handle-hx-post request-route)

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

(defn ring-request->request [ring-request]
  {:request/route (route ring-request)
   :request/hx? (hx? ring-request)
   :session/id (ring-request :session/id)
   :request/form-data (-> ring-request :form-params str-keys->keywords)})

;; 
;; 
;; 
;; 
;; 
;; 
;; 

(defn set-cookie-value [key value]
  (str key "=" value
       "; Path=/"
       "; HttpOnly"
       "; SameSite=Strict"
       #_"; Secure"
       "; Max-Age=31536000"))

(defn assoc-set-cookie [ring-response key value]
  (assoc-in ring-response [:headers "Set-Cookie"] (set-cookie-value key value)))

(defn get-cookie [ring-request key]
  (get-in ring-request [:cookies key :value]))

(defn wrap-session-id [handler]
  (fn [ring-request]
    (let [session-id (get-cookie ring-request "session-id")
          session-id-final (or session-id (session/random-session-id!))
          ring-request (assoc ring-request :session/id session-id-final)
          ring-response (handler ring-request)
          ring-response-final (if session-id
                                ring-response
                                (assoc-set-cookie ring-response "session-id" session-id-final))]
      ring-response-final)))


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


(defn html-headers [response]
  {"Content-Type" "text/html"
   "Cache-Control" "no-store, max-age=0"
   "HX-Push-Url" (:response/hx-push-url response)})

(defmethod response->ring-response :response-type/html [response]
  {:status (status response)
   :headers (html-headers response)
   :body (html-body response)})

(defn- append-doc-type [html]
  (str "<!doctype html>" html))

(def html-document-body (comp append-doc-type html-body))

(defmethod response->ring-response :response-type/html-document [response]
  {:status (status response)
   :headers (html-headers response)
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

(defn hx-push-url [response url]
  (assoc response :response/hx-push-url url))

(defn hx-push-route [response route]
  (hx-push-url response (route/encode route)))

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
