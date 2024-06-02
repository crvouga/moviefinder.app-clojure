(ns app.requests
  (:require [clojure.string]
            [hiccup2.core]
            [app.route]
            [app.base64]))

(defn- remove-leading-backslash [uri]
  (if (clojure.string/starts-with? uri "/") (subs uri 1) uri))

(defn ring-request->request [ring-request]
  {:request/route (-> ring-request :uri remove-leading-backslash app.route/decode)
   :request/hx-request? (boolean (get-in ring-request [:headers "hx-request"]))})

(defmulti route-hx (fn [request] (-> request :request/route :route/name)))

(defn response->ring-response [response]
  (let [view (get response :view)]
    (if view
      (let [body (get response :body)]
        {:status (get response :status 200)
         :headers (get response :headers {})
         :body (if body body (str (hiccup2.core/html view)))})
      {:status 404
       :headers {}
       :body "Not Found"})))


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
