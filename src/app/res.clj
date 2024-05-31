(ns app.res
  (:require [clojure.string]
            [hiccup2.core]))

(defn remove-leading-backslash [uri]
  (if (clojure.string/starts-with? uri "/") (subs uri 1) uri))

(defn ring-req->req [ring-req]
  {:req-name (-> ring-req :uri remove-leading-backslash)})

(defmulti req->res :req-name)

(defn html [html-content]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str (hiccup2.core/html html-content))})