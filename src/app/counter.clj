(ns app.counter
  (:require [app.view] 
            [app.route]
            [app.requests]))

(def clicks! (atom 0))

(defmethod app.requests/route-hx ::clicked-append [_]
  (swap! clicks! inc)
  (app.requests/html [:p "Clicked!"]))

(defmethod app.requests/route-hx ::clicked-clear [_]
  (reset! clicks! 0)
  (app.requests/html ""))


(defn view-counter-panel []
  [:div
   [:h1 "Counter"]

   (app.view/button
    {:hx-get (app.route/encode {:route/name ::clicked-clear}) 
     :hx-swap "innerHTML" 
     :hx-target (str "#" "counter-clicks")}
    "Clear")

   (app.view/button
    {:hx-post (app.route/encode {:route/name ::clicked-append}) 
     :hx-swap "beforeend" 
     :hx-target (str "#" "counter-clicks")}
    "Append")

   [:div {:id "counter-clicks"}
    (for [_ (range @clicks!)]
      [:p "Clicked!"])]])

(defn view-couter-index []
  (app.view/view-app-tabs-layout :counter/index (view-counter-panel)))

(defmethod app.requests/route-hx :counter/index [_]
  (app.requests/html (view-couter-index)))
