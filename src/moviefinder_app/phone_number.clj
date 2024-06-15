(ns moviefinder-app.phone-number 
  (:require [clojure.string :as str]))


(def regex #"^\+\d{1,3}-\d{10}$")

(defn ensure-country-code [phone-number]
  (if (re-matches regex phone-number)
    phone-number
    (str "+1" phone-number)))

(defn normalize [phone-number]
  (-> phone-number
      (str/replace #"\D" "")
      (ensure-country-code)))