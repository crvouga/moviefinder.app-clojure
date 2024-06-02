(ns app.counter
  (:require [app.view] 
            [app.res]))

(def clicks! (atom 0))

(defmethod app.res/handle ::clicked-append [_]
  (swap! clicks! inc)
  (app.res/html [:p "Clicked!"]))

(defmethod app.res/handle ::clicked-clear [_]
  (reset! clicks! 0)
  (app.res/html ""))


(defn view-counter-panel []
  [:div
   [:h1 "Counter"]

   (app.view/button
    {:hx-get (app.res/encode-route {:route/name ::clicked-clear}) 
     :hx-swap "innerHTML" 
     :hx-target (str "#" "counter-clicks")}
    "Clear")

   (app.view/button
    {:hx-post (app.res/encode-route {:route/name ::clicked-append}) 
     :hx-swap "beforeend" 
     :hx-target (str "#" "counter-clicks")}
    "Append")

   [:div {:id "counter-clicks"}
    (for [_ (range @clicks!)]
      [:p "Clicked!"])]])

(defn view-couter-index []
  (app.view/view-app-tabs-layout :counter/index (view-counter-panel)))

(defmethod app.res/handle :counter/index [_]
  (app.res/html (view-couter-index)))
