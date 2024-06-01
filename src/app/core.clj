(ns app.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [app.view]
            [app.feed]
            [app.counter]
            [app.res]))

(defn view-app [req]
  [:div.w-full.h-full.flex.flex-col.overflow-hidden
   (app.view/tab-panel 
    (condp = (:name req)
      app.counter/route-counter (app.counter/view-counter)
      app.feed/route-feed (app.feed/view-feed)
      :else ""))
   (app.view/tabs
    (app.view/tab app.counter/route-counter "Counter")
    (app.view/tab app.feed/route-feed "Feed"))])

(defn view [req]
  [:html {:lang "en" :doctype :html5}
   [:head
    [:title "moviefinder.app"]
    [:meta {:name :description :content "Find movies to watch"}]
    [:meta {:charset "utf-8"}]
    [:meta {:name :viewport :content "width=device-width, initial-scale=1.0"}]
    [:script {:src "https://cdn.tailwindcss.com"}]
    [:script {:src "https://unpkg.com/htmx.org@1.9.12"}]]
   [:body.bg-neutral-950.text-white {:hx-boost true}
    [:div
     {:class "fixed left-1/2 top-1/2 transform -translate-x-1/2 -translate-y-1/2 w-screen h-screen flex flex-col items-center justify-center"}
     [:div {:class "relative flex h-full max-h-[915px] w-full max-w-[520px] flex-col items-center justify-center overflow-hidden rounded border border-neutral-700"}
      (view-app req)]]]])

  (defmethod app.res/req->res :default [req]
    (app.res/html [:p (str "404 Not Found" (:name req))]))

  (defn ring-req->res [ring-req]
    (let [req (app.res/ring-req->req ring-req)
          res (if (:hx-request? req) (app.res/req->res req) (app.res/html-document (view req)))]
      (println req)
      res))

  (defn handler [ring-req]
    (let [res (ring-req->res ring-req)]
      res))

  (defn -main []
    (run-jetty (wrap-reload #'handler) {:port 3000 :join? false}))