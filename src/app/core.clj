(ns app.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [app.view]
            [app.feed]
            [app.counter]
            [app.res]))

(defn view-app [req]
  [:div.w-full.h-full.flex.flex-col
   (str "Request: " (:name req))
   (app.view/tabs
    (app.view/tab {:hx-get (str app.counter/req-name-counter) :hx-target "#tab" :hx-swap "innerHTML"} "Counter")
    (app.view/tab {:hx-get (str app.counter/req-name-counter) :hx-target "#tab" :hx-swap "innerHTML"} "Counter"))
   [:div#tab]])

(defn view [req]
  [:html {:lang "en" :doctype :html5}
   [:head
    [:title "moviefinder.app"]
    [:meta {:name :description :content "Find movies to watch"}]
    [:meta {:charset "utf-8"}]
    [:meta {:name :viewport :content "width=device-width, initial-scale=1.0"}]
    [:script {:src "https://cdn.tailwindcss.com"}]
    [:script {:src "https://unpkg.com/htmx.org@1.9.12"}]]
   [:body.bg-neutral-950.text-white 
    [:div.flex.justify-center.items-center.h-screen.max-w-xl.mx-auto.border.border-netural-800.rounded-lg
     (view-app req)]]])



(defmethod app.res/req->res :default [req] 
  (app.res/html-document (view req)))


(defn ring-req->res [ring-req]
  (let [req (app.res/ring-req->req ring-req)
        res (app.res/req->res req)]
    (println req)
    (println res)
    res))

(defn handler [ring-req]
  (let [res (ring-req->res ring-req)]
    res))

(defn -main []
  (run-jetty (wrap-reload #'handler) {:port 3000 :join? false}))