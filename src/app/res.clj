(ns app.res
  (:require [clojure.string]
            [hiccup2.core]
            [app.base64]))

(defn remove-leading-backslash [uri]
  (if (clojure.string/starts-with? uri "/") (subs uri 1) uri))

(defn remove-leading-colon [uri]
  (if (clojure.string/starts-with? uri ":") (subs uri 1) uri))

(defn ring-req->req [ring-req]
  {:name (-> ring-req :uri remove-leading-backslash remove-leading-colon keyword)
   :hx-request? (boolean (get-in ring-req [:headers "hx-request"]))})

(defmulti req->res :name)

(defn html [html-content]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str (hiccup2.core/html html-content))})

(defn html-document [html-content]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str "<!doctype html>" (hiccup2.core/html html-content))})

(defn route->url [keyword]
  (str "/" keyword))