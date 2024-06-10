(ns moviefinder-app.db
  (:require [clojure.java.jdbc :as jdbc]
            [moviefinder-app.env :as env]
            [honey.sql :as sql]
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
  (let [dbname (-> database-url
                   (str/split #"/")
                   last
                   (str/split #"\?")
                   first)]
    {:dbname dbname}))

(defn remove-quotes [s]
  (->> (str/split s #"\"")
       (filter #(not= % "\""))
       (apply str)))

(defn extract-params
  "Extracts the query parameters from the database URL."
  [database-url]
  (let [entries (-> database-url
                    (str/split #"\?")
                    last
                    (str/split #"&"))
        params (->> entries
                    (map #(str/split % #"="))
                    (map (fn [[k v]] [(keyword k) (remove-quotes v)]))
                    (into {}))]
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
  (query [this sql-map])
  (execute! [this sql-map]))


(defrecord DbConnImpl [db-spec]
  DbConn
  (query [_this sql-map]
    (jdbc/query db-spec (sql/format sql-map)))
  (execute! [_this sql-map]
    (jdbc/execute! db-spec (sql/format sql-map))))

(def conn (->DbConnImpl db-spec))