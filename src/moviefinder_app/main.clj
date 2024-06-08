(ns moviefinder-app.main
  (:require [moviefinder-app.env :as env]
            [moviefinder-app.account]
            [moviefinder-app.counter]
            [moviefinder-app.home]
            [moviefinder-app.login.send-login-link]
            [moviefinder-app.login.use-login-link]
            [moviefinder-app.movie.details]
            [moviefinder-app.requests :as requests]
            [moviefinder-app.deps :as deps]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
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
      (-> request requests/handle-hx :response/view)]]]])

(defmethod requests/handle-hx :default [request]
  (-> request
      (assoc :request/route {:route/name :route/home})
      requests/handle-hx))


(defmethod requests/handle :default [request]
  (requests/html-document (view request)))

;; 
;; 
;; 
;; 
;; 
;; 
;; 

(defn handle [request]
  (if (:request/hx? request)
    (requests/handle-hx request)
    (requests/handle request)))

(defn tap [x]
  (println x)
  x)

(def deps (deps/deps-real))

(defn assoc-deps [request]
  (merge request deps))

(defn handle-ring-request [ring-request]
  (-> ring-request
      requests/ring-request->request
      tap
      assoc-deps
      handle
      requests/response->ring-response))

(defn run-server! [input]
  (-> #'handle-ring-request
      (wrap-params)
      (wrap-session)
      (wrap-reload)
      (run-jetty {:port (input :server/port) :join? false})))


(def port (-> (moviefinder-app.env/get! "PORT") Integer/parseInt))
(def base-url (moviefinder-app.env/get! "BASE_URL"))
(defn -main []
  (run-server! {:server/port port})
  (println (str "Server running at " base-url)))