(ns app.counter
  (:require [app.view] 
            [app.res]))

(def clicks! (atom 0))

(defmethod app.res/req->res ::clicked-append [_]
  (swap! clicks! inc)
  (app.res/html [:p "Clicked!"]))

(defmethod app.res/req->res ::clicked-clear [_]
  (reset! clicks! 0)
  (app.res/html ""))

(defn view-counter []
  [:div
   [:h1 "Counter"]

   (app.view/button
    {:hx-get (app.res/route->url ::clicked-clear) :hx-swap "innerHTML" :hx-target (str "#" "counter-clicks")}
    "Clear")

   (app.view/button
    {:hx-post (app.res/route->url ::clicked-append) :hx-swap "beforeend" :hx-target (str "#" "counter-clicks")}
    "Append")

   [:div {:id "counter-clicks"}
    (for [_ (range @clicks!)]
      [:p "Clicked!"])]])



(def route-counter ::counter)

(defmethod app.res/req->res route-counter [_]
  (app.res/html (view-counter)))
