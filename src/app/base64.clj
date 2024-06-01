(ns app.base64
  (:import (java.util Base64)))

(defn encode [s]
  (let [encoder (Base64/getEncoder)
        bytes (.getBytes s "UTF-8")
        encoded-bytes (.encode encoder bytes)]
    (String. encoded-bytes "UTF-8")))

(defn decode [s]
  (let [decoder (Base64/getDecoder)
        bytes (.getBytes s "UTF-8")
        decoded-bytes (.decode decoder bytes)]
    (String. decoded-bytes "UTF-8")))
