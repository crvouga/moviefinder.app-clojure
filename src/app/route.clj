(ns app.route
  (:require [clojure.string]
            [hiccup2.core]
            [app.base64]))

(def add-leading-backslash (partial str "/"))

(defn encode [route]
  (-> route pr-str app.base64/encode add-leading-backslash))

(defn decode [route]
  (try
    (-> route app.base64/decode read-string)
    (catch Exception _
      nil)))

