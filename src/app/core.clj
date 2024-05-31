(ns app.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [hiccup2.core]))

(defn html [html]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str (hiccup2.core/html html))})

(defn button 
  [props & children]
  [:button.bg-blue-500.hover:bg-blue-700.text-white.font-bold.py-2.px-4.rounded.active:opacity-50 props children])


(defn ring-request->request [ring-request]
  {:name (:uri ring-request)})

(defn handler [ring-request]
  (let [request (ring-request->request ring-request)]
    (print request))
  
  (html
   [:html
    [:head
     [:title "COOL"]
     [:script {:src "https://cdn.tailwindcss.com"}]
     [:script {:src "https://unpkg.com/htmx.org@1.9.12"}]]
    [:body.bg-neutral-950.text-white {:hx-boost true}
     [:h1 "COOL"] 
     (button {:hx-get ::clicked-append :hx-swap :afterend} "Click me")]]))

(defn -main []
  (run-jetty (wrap-reload #'handler) {:port 3000 :join? false}))
