(ns moviefinder.app.sql
  (:require [clojure.java.jdbc]))

(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "db/database.db"})

(defn print-result-set
  "prints the result set in tabular form"
  [result-set]
  (doseq [row result-set]
    (println row)))

(defn query
  "execute query and return lazy sequence"
  [sql-query]
  (clojure.java.jdbc/query db [sql-query]))
