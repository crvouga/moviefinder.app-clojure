(ns app.ui)

(defn button
  [props & children]
  [:button.bg-blue-500.hover:bg-blue-700.text-white.font-bold.py-2.px-4.rounded.active:opacity-50 props children])
