(ns app.counter
  (:require [app.view] 
            [app.res]
            [app.routes]))

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
    {:hx-get (app.res/keyword->url ::clicked-clear) 
     :hx-swap "innerHTML" 
     :hx-target (str "#" "counter-clicks")}
    "Clear")

   (app.view/button
    {:hx-post (app.res/keyword->url ::clicked-append) 
     :hx-swap "beforeend" 
     :hx-target (str "#" "counter-clicks")}
    "Append")

   [:div {:id "counter-clicks"}
    (for [_ (range @clicks!)]
      [:p "Clicked!"])]])

(defn view-couter-route []
  (app.routes/view-app-tabs-layout app.routes/route-counter (view-counter-panel)))

(defmethod app.res/handle app.routes/route-counter [_]
  (app.res/html (view-couter-route)))
