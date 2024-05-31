(ns app.core
  (:require [clojure.string]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [app.ui]
            [app.res :refer [req->res html html-document]]))

(def clicks! (atom 0))

(defmethod req->res (str ::clicked-append) [_]
  (swap! clicks! inc)
  (html [:p "Clicked!"]))

(defmethod req->res (str ::clicked-clear) [_]
  (reset! clicks! 0)
  (html ""))

(defn view-app []
  [:div
   [:h1 "App"]
   
   (app.ui/button
    {:hx-get (str ::clicked-clear) :hx-swap "innerHTML" :hx-target (str "#" "clicks")}
    "Clear")
   
   (app.ui/button
    {:hx-post (str ::clicked-append) :hx-swap "beforeend" :hx-target (str "#" "clicks")}
    "Append")
  
   [:div#clicks
    (for [_ (range @clicks!)]
      [:p "Clicked!"])]])

(defmethod req->res (str ::app) [_]
  (html (view-app)))

(defmethod req->res :default [_]
  (html-document 
   [:html {:lang "en" :doctype :html5}
    [:head
     [:title "moviefinder.app"]
     [:meta {:name :description :content "Find movies to watch"}]
     [:meta {:charset "utf-8"}]
     [:meta {:name :viewport :content "width=device-width, initial-scale=1.0"}]
     [:script {:src "https://cdn.tailwindcss.com"}]
     [:script {:src "https://unpkg.com/htmx.org@1.9.12"}]]
    [:body.bg-neutral-950.text-white {:hx-boost true }
     [:div.flex.justify-center.items-center.h-screen.max-w-xl.mx-auto.border.border-netural-800.rounded-lg
      (view-app)]]]))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(def state! (atom {}))

(defn ring-req->res [ring-req]
  (let [req (app.res/ring-req->req ring-req)
        res (req->res req)]
    res))

(defn handler [ring-req]
  (let [res (ring-req->res ring-req)]
    res))

(defn -main []
  (run-jetty (wrap-reload #'handler) {:port 3000 :join? false}))