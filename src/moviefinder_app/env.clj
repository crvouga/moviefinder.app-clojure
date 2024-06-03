(ns moviefinder-app.env 
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn- parse-env-file! [file-path]
  (with-open [reader! (io/reader file-path)]
    (let [lines (line-seq reader!)
          key-val-pairs (map #(str/split % #"=" 2) lines)
          env-vars (into {} key-val-pairs)]
      env-vars)))

(defn load! [file-path]
  (let [env-vars (parse-env-file! file-path)]
    (doseq [[key val] env-vars]
      (System/setProperty key val))))

(try
  (load! ".env")
  (catch Exception e
    (println "Error loading .env file" e)))

(doseq [key (keys (System/getenv))
        :let [val (System/getenv key)]
        :when (not (nil? val))]
  (System/setProperty key val))

(defn get-env-var! [key]
  (let [env-var (or (System/getenv key) (System/getProperty key))]
    (if (nil? env-var)
      (throw (Exception. (str key " not set")))
      env-var)))