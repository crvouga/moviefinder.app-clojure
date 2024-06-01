(ns app.res
  (:require [clojure.string]
            [hiccup2.core]
            [app.base64]))

(defn- remove-leading-backslash [uri]
  (if (clojure.string/starts-with? uri "/") (subs uri 1) uri))

(defn- remove-leading-colon [uri]
  (if (clojure.string/starts-with? uri ":") (subs uri 1) uri))

(defn ring-req->req [ring-req]
  {::route (-> ring-req :uri remove-leading-backslash remove-leading-colon keyword)
   :hx-request? (boolean (get-in ring-req [:headers "hx-request"]))})

(defn req->route [req]
  (::route req))

(defmulti req->res req->route)

(defn html [hiccup]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :hiccup hiccup
   :body (str (hiccup2.core/html hiccup))})

(defn html-document [hiccup]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :hiccup hiccup
   :body (str "<!doctype html>" (hiccup2.core/html hiccup))})


(defn keyword->url [keyword]
  (str "/" keyword))