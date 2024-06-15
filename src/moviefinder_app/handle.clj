(ns moviefinder-app.handle
  (:require [clojure.string :as string]
            [hiccup2.core :as hiccup]
            [moviefinder-app.route :as route]
            [ring.util.response :as ring-response]))


;; 
;; 
;; 
;; Routing
;; 
;; 
;; 

(def request-route-name (comp :route/name :request/route))

(defmulti hx-get request-route-name)

(defmulti hx-post request-route-name)

(defmulti handle request-route-name)

(defn handle-hx-get-push [request]
  (-> request
      hx-get
      (assoc :response/hx-push-url (route/encode (:request/route request)))))

;; 
;; 
;; 
;; 

(defn get? [request]
  (= :get (:request/method request)))

(defn post? [request]
  (= :post (:request/method request)))

(defn hx? [request]
  (:request/hx? request))

(defn hx-get? [request]
  (and (hx? request) (get? request)))

(defn hx-post? [request]
  (and (hx? request) (post? request)))

(defn root-handle [request]
  (cond
    (hx-get? request) (hx-get request)
    (hx-post? request) (hx-post request)
    :else (handle request)))
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
   :request/hx? (boolean (get-in ring-request [:headers "hx-request"]))
   :request/method (-> ring-request :request-method name keyword)
   :session/id (ring-request :session/id)
   :request/form-data (-> ring-request :form-params str-keys->keywords)})



;; 
;; 
;; 
;; Response
;; 
;; 
;; 

(defmulti response->ring-response :response/type)

(defmethod response->ring-response :default [_response]
  {:status 500
   :headers {}
   :body "Internal Server Error"})

(defn- status [response]
  (if (:response/ok? response) 200 500))

(defn- append-doc-type [html]
  (str "<!DOCTYPE html>" html))

(defn- html-body [response] 
  (-> response
      :response/view
      hiccup/html
      str
      append-doc-type))

(defn- html-headers [response]
  {"Content-Type" "text/html"
   "Cache-Control" "no-store, max-age=0"
   "HX-Push-Url" (:response/hx-push-url response)})

(defmethod response->ring-response :response-type/html [response]
  {:status (status response)
   :headers (html-headers response)
   :body (html-body response)})

(defn html [request view-fn]
  (merge request
         {:response/ok? true
          :response/view (view-fn request)
          :response/type :response-type/html}))


(defn hx-push-url [response url]
  (assoc response :response/hx-push-url url))

(defn hx-push-route [response route]
  (hx-push-url response (route/encode route)))

(defn hx-push-request-route [input]
  (-> input
      (hx-push-route (:request/route input))))

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
