(ns moviefinder.counter
  (:require [moviefinder.view] 
            [moviefinder.route]
            [moviefinder.requests]))

(def clicks! (atom 0))

(defmethod moviefinder.requests/route-hx ::clicked-append [_]
  (swap! clicks! inc)
  (moviefinder.requests/html [:p "Clicked!"]))

(defmethod moviefinder.requests/route-hx ::clicked-clear [_]
  (reset! clicks! 0)
  (moviefinder.requests/html ""))


(defn view-counter-panel []
  [:div
   [:h1 "Counter"]

   (moviefinder.view/button
    {:hx-get (moviefinder.route/encode {:route/name ::clicked-clear}) 
     :hx-swap "innerHTML" 
     :hx-target (str "#" "counter-clicks")}
    "Clear")

   (moviefinder.view/button
    {:hx-post (moviefinder.route/encode {:route/name ::clicked-append}) 
     :hx-swap "beforeend" 
     :hx-target (str "#" "counter-clicks")}
    "Append")

   [:div {:id "counter-clicks"}
    (for [_ (range @clicks!)]
      [:p "Clicked!"])]])

(defn view-couter-index []
  (moviefinder.view/view-app-tabs-layout :counter/index (view-counter-panel)))

(defmethod moviefinder.requests/route-hx :counter/index [_]
  (moviefinder.requests/html (view-couter-index)))
