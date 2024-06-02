(ns app.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [app.view]
            [app.account]
            [app.feed]
            [app.counter]
            [app.requests]))
;; 
;; 
;; 
;; 
;; 
;; 
;; 
;; 

(defn view [request]
  [:html {:lang "en" :doctype :html5}
   [:head
    [:title "moviefinder.app"]
    [:meta {:name :description :content "Find movies to watch"}]
    [:meta {:charset "utf-8"}]
    [:meta {:name :viewport :content "width=device-width, initial-scale=1.0"}]
    [:script {:src "https://cdn.tailwindcss.com"}]
    [:script {:src "https://unpkg.com/htmx.org@1.9.12"}]
    [:script {:src "https://cdn.jsdelivr.net/npm/swiper@11/swiper-element-bundle.min.js"}]]
   
   [:body.bg-neutral-950.text-white {:hx-boost true :hx-target "#app" :hx-swap "innerHTML"}
    [:div
     {:class "fixed left-1/2 top-1/2 transform -translate-x-1/2 -translate-y-1/2 w-screen h-[100dvh] flex flex-col items-center justify-center"}
     [:div {:id "app" :class "relative flex h-full max-h-[915px] w-full max-w-[520px] flex-col items-center justify-center overflow-hidden rounded border border-neutral-700"}
      (-> request app.requests/route-hx :view)]]]])

  (defmethod app.requests/route-hx :default [request]
    (-> request 
        (assoc :request/route {:route/name :home/home}) 
        app.requests/route-hx))


;; 
;; 
;; 
;; 
;; 
;; 
;; 
;; 
;; 



  
(defn handle-ring-request [ring-request]
    (let [request (app.requests/ring-request->request ring-request)
          response (if (:request/hx-request? request) 
                (app.requests/route-hx request) 
                (app.requests/html-document (view request)))]
      (println request) 
      response))


(defn get-port! [] 
    (when-let [port (System/getenv "PORT")]
      (Integer. port)))
  
(defn -main []
    (let [port (or (get-port!) 3000)]
      (run-jetty (wrap-reload #'handle-ring-request) {:port port :join? false})
      (println (str "Server listening on port " port "..."))))