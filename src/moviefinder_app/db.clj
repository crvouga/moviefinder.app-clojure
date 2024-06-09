(ns moviefinder-app.db
  (:require [clojure.java.jdbc :as jdbc]
            [moviefinder-app.env :as env]
            [clojure.string :as str]))

(defn extract-user-info
  "Extracts the username and password from the database URL."
  [database-url]
  (let [user-info (second (re-find #"postgresql://([^@]+)@" database-url))
        [user password] (str/split user-info #":" 2)]
    {:user user :password password}))

(defn extract-host-and-port
  "Extracts the host and port from the database URL."
  [database-url]
  (let [host-and-port (second (re-find #"@([^/]+)" database-url))
        [host port] (str/split host-and-port #":" 2)]
    {:host host :port (Integer. port)}))

(defn extract-dbname
  "Extracts the database name from the database URL."
  [database-url]
  (let [dbname (second (re-find #"/([^?]+)" database-url))]
    {:dbname dbname}))

(defn extract-params
  "Extracts the query parameters from the database URL."
  [database-url]
  (let [query-string (second (re-find #"\?(.+)$" database-url))
        params (when query-string
                 (into {} (map #(str/split % #"=")
                               (str/split query-string #"&"))))]
    params))

(def database-url (env/get! "DATABASE_URL"))

(defn parse-database-url
  "Parses the DATABASE_URL into a db-spec map."
  [database-url]
  (merge (extract-user-info database-url)
         (extract-host-and-port database-url)
         (extract-dbname database-url)
         (extract-params database-url)
         {:dbtype "postgresql"}))

(def db-spec (parse-database-url database-url))

(defprotocol DbConn
  (query [this sql-str])
  (execute! [this sql-str]))


(defrecord DbConnImpl [db-spec]
  DbConn
  (query [_this sql-str]
    (jdbc/query db-spec [sql-str]))
  (execute! [_this sql-str]
    (jdbc/execute! db-spec [sql-str])))

(def conn (->DbConnImpl db-spec))