(ns app.counter
  (:require [app.view] 
            [app.res]))

(def clicks! (atom 0))

(defmethod app.res/req->res (str ::clicked-append) [_]
  (swap! clicks! inc)
  (app.res/html [:p "Clicked!"]))

(defmethod app.res/req->res (str ::clicked-clear) [_]
  (reset! clicks! 0)
  (app.res/html ""))

(defn view-counter []
  [:div
   [:h1 "Counter"]

   (app.view/button
    {:hx-get (str "/" ::clicked-clear) :hx-swap "innerHTML" :hx-target (str "#" "counter-clicks")}
    "Clear")

   (app.view/button
    {:hx-post (str "/" ::clicked-append) :hx-swap "beforeend" :hx-target (str "#" "counter-clicks")}
    "Append")

   [:div {:id "counter-clicks"}
    (for [_ (range @clicks!)]
      [:p "Clicked!"])]])



(def req-name-counter ::counter)

(defmethod app.res/req->res (str req-name-counter) [_]
  (app.res/html (view-counter)))
