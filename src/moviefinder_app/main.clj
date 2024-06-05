(ns moviefinder-app.main
  (:require [moviefinder-app.account]
            [moviefinder-app.counter]
            [moviefinder-app.home]
            [moviefinder-app.movie.details]
            [moviefinder-app.requests]
            [moviefinder-app.view]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.session :refer [wrap-session]]))
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
    [:link {:rel "icon" :href "data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 36 36'><text y='32' font-size='32'>🍿</text></svg>"}]
    [:meta {:name :viewport :content "width=device-width, initial-scale=1.0"}]
    [:script {:src "https://cdn.tailwindcss.com"}]
    [:script {:src "https://unpkg.com/htmx.org@1.9.12"}]
    [:script {:src "https://cdn.jsdelivr.net/npm/swiper@11/swiper-element-bundle.min.js"}]]

   [:body.bg-neutral-950.text-white {:hx-boost true :hx-target "#app" :hx-swap "innerHTML"}
    [:div
     {:class "fixed left-1/2 top-1/2 transform -translate-x-1/2 -translate-y-1/2 w-screen h-[100dvh] flex flex-col items-center justify-center"}
     [:div {:id "app" :class "relative flex h-full max-h-[915px] w-full max-w-[520px] flex-col items-center justify-center overflow-hidden rounded border border-neutral-700"}
      (-> request moviefinder-app.requests/handle-hx :response/view)]]]])

(defmethod moviefinder-app.requests/handle-hx :default [request]
  (-> request
      (assoc :request/route {:route/name :home/home})
      moviefinder-app.requests/handle-hx))


(defmethod moviefinder-app.requests/handle :default [request]
  (moviefinder-app.requests/html-document (view request)))
  
;; 
;; 
;; 
;; 
;; 
;; 
;; 
;; 
;; 


(defn handle [request]
  (if (:request/hx-request? request)
    (moviefinder-app.requests/handle-hx request)
    (moviefinder-app.requests/handle request)))

(defn tap [x]
  (println x)
  x)

(defn handle-ring-request [ring-request]
  (-> ring-request
      moviefinder-app.requests/ring-request->request
      tap
      handle
      moviefinder-app.requests/response->ring-response))

(defn get-port! []
  (when-let [port (System/getenv "PORT")]
    (Integer. port)))

(defn run-server! [input]
  (-> #'handle-ring-request
      (wrap-session)
      (wrap-reload)
      (run-jetty {:port (input :server/port) :join? false})))

(defn -main []
  (let [port (or (get-port!) 8888)]
    (run-server! {:server/port port})
    (println (str "Server listening on port " port "..."))
    (println (str "http://localhost:" port))))