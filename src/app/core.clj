(ns app.core
  (:require [clojure.string]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [app.view]
            [app.res]))

(def clicks! (atom 0))

(defmethod app.res/req->res (str ::clicked-append) [_]
  (swap! clicks! inc)
  (app.res/html [:p "Clicked!"]))

(defmethod app.res/req->res (str ::clicked-clear) [_]
  (reset! clicks! 0)
  (app.res/html))

(defn view-app []
  [:div
   [:h1 "App"]
   
   (app.view/button
    {:hx-get (str ::clicked-clear) :hx-swap "innerHTML" :hx-target (str "#" "clicks")}
    "Clear")
   
   (app.view/button
    {:hx-post (str ::clicked-append) :hx-swap "beforeend" :hx-target (str "#" "clicks")}
    "Append")
  
   [:div#clicks
    (for [_ (range @clicks!)]
      [:p "Clicked!"])]])

(defmethod app.res/req->res (str ::app) [_]
  (app.res/html (view-app)))

(defmethod app.res/req->res :default [_]
  (app.res/html-document 
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


(def state! (atom {}))

(defn ring-req->res [ring-req]
  (let [req (app.res/ring-req->req ring-req)
        res (app.res/req->res req)]
    res))

(defn handler [ring-req]
  (let [res (ring-req->res ring-req)]
    res))

(defn -main []
  (run-jetty (wrap-reload #'handler) {:port 3000 :join? false}))