(ns moviefinder-app.db-migration
  #_{:clj-kondo/ignore [:unused-namespace]}
  (:require [clojure.java.io :as io]
            [clojure.java.shell :refer [sh]]
            [clojure.string :as str])) 

(def dbmate-version "1.14.0")

(defn db-up! []
  (let [filename (str "dbmate-" dbmate-version)
        url (format "https://github.com/amacneil/dbmate/releases/download/v%s/dbmate-macos-amd64" dbmate-version)
        filepath (str (System/getProperty "user.dir") "/" filename)]
    (println "Downloading dbmate version" dbmate-version "...")
    (with-open [in (io/input-stream url)
                out (io/output-stream filepath)]
      (io/copy in out))
    (.setExecutable (io/file filepath) true)
    (sh filepath "--version")
    (sh filepath "up")
    (io/delete-file filepath))) 