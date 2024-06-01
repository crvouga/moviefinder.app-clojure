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
    
    (app.view/tab {:hx-get (str "/" app.counter/req-name-counter) 
                   :hx-target "#tab" 
                   :hx-swap "innerHTML"
                   :hx-push-url (str "/" app.counter/req-name-counter)} 
                  "Counter")
    
    (app.view/tab {:hx-get (str "/" app.counter/req-name-counter) 
                   :hx-target "#tab" 
                   :hx-swap "innerHTML"
                   :hx-push-url (str "/" app.counter/req-name-counter)} 
                  "Counter"))
   [:div#tab
    (cond
      (= (:name req) (str app.counter/req-name-counter)) (app.counter/view-counter)
      :else "")]])

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


(defn default-res [req]
  (app.res/html-document (view req)))

  (defmethod app.res/req->res :default [_req]
    (app.res/html [:p "404 Not Found"]))

  (defn ring-req->res [ring-req]
    (let [req (app.res/ring-req->req ring-req)]
      (cond
        (:hx-request? req) (app.res/req->res req)
        :else (default-res req))))

  (defn handler [ring-req]
    (let [res (ring-req->res ring-req)]
      res))

  (defn -main []
    (run-jetty (wrap-reload #'handler) {:port 3000 :join? false}))