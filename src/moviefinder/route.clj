(ns moviefinder.route
  (:require [clojure.string]
            [hiccup2.core]
            [moviefinder.base64]))

(def add-leading-backslash (partial str "/"))

(defn encode [route]
  (-> route pr-str moviefinder.base64/encode add-leading-backslash))

(defn decode [route]
  (try
    (-> route moviefinder.base64/decode read-string)
    (catch Exception _
      nil)))

