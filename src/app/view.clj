(ns app.view)

(defn button
  [props & children]
  [:button.bg-blue-600.hover:bg-blue-700.text-white.font-bold.py-2.px-4.rounded.active:opacity-50 props children])

(defn tab [props & children]
  [:a.flex-1.p-4.flex.items-center.justify-center props children])

(defn tabs [props & children]
  [:nav.flex.w-full props children])