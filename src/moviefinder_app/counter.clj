(ns moviefinder-app.counter
  (:require [moviefinder-app.view] 
            [moviefinder-app.route]
            [moviefinder-app.requests]))

(def clicks! (atom 0))

(defmethod moviefinder-app.requests/route-hx ::clicked-append [_]
  (swap! clicks! inc)
  (moviefinder-app.requests/html [:p "Clicked!"]))

(defmethod moviefinder-app.requests/route-hx ::clicked-clear [_]
  (reset! clicks! 0)
  (moviefinder-app.requests/html ""))


(defn view-counter-panel []
  [:div
   [:h1 "Counter"]

   (moviefinder-app.view/button
    {:hx-get (moviefinder-app.route/encode {:route/name ::clicked-clear}) 
     :hx-swap "innerHTML" 
     :hx-target (str "#" "counter-clicks")}
    "Clear")

   (moviefinder-app.view/button
    {:hx-post (moviefinder-app.route/encode {:route/name ::clicked-append}) 
     :hx-swap "beforeend" 
     :hx-target (str "#" "counter-clicks")}
    "Append")

   [:div {:id "counter-clicks"}
    (for [_ (range @clicks!)]
      [:p "Clicked!"])]])

(defn view-couter-index []
  (moviefinder-app.view/view-app-tabs-layout :counter/index (view-counter-panel)))

(defmethod moviefinder-app.requests/route-hx :counter/index [_]
  (moviefinder-app.requests/html (view-couter-index)))
