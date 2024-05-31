(ns app.core
  (:require [clojure.string]
            [hiccup2.core :refer [html]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [app.ui]))

(defn res-html [html-content]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str (html html-content))})

(defmulti ->res :req-name)

(defmethod ->res (str ::clicked-append) [_]
  (res-html [:p "Clicked!"]))

(defmethod ->res (str ::app) [_]
  (res-html
   [:div
    [:h1 "App"]
    (app.ui/button {:hx-get (str ::clicked-append) :hx-swap "afterend"} "Click me")]))

(defmethod ->res :default [_]
  (res-html
   [:html
    [:head
     [:title "moviefinder.app"]
     [:script {:src "https://cdn.tailwindcss.com"}]
     [:script {:src "https://unpkg.com/htmx.org@1.9.12"}]]
    [:body.bg-neutral-950.text-white 
     {:hx-boost true :hx-get (str ::app) :hx-swap "innerHTML" :hx-trigger "load"}
     [:p "Loading..."]]]))

(defn remove-leading-backslash [uri]
  (if (clojure.string/starts-with? uri "/") (subs uri 1) uri))

(defn ring-req->req [ring-req]
  {:req-name (-> ring-req :uri remove-leading-backslash)})

(defn handler [ring-req]
  (let [req (ring-req->req ring-req)
        response (->res req)]
    (println req)
    response))

(defn -main []
  (run-jetty (wrap-reload #'handler) {:port 3000 :join? false}))