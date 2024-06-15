(ns moviefinder-app.db
  (:require [clojure.java.jdbc :as jdbc]
            [moviefinder-app.env :as env]
            [honey.sql :as sql]
            [clojure.string :as str]))

(def database-url (env/get! "DATABASE_URL"))

(defn parse-database-url [database-url]
  (let [user-info (second (re-find #"postgresql://([^@]+)@" database-url))
        [user password] (str/split user-info #":" 2)
        host-and-port (second (re-find #"@([^/]+)" database-url))
        [host port] (str/split host-and-port #":" 2)
        dbname (-> database-url
                   (str/split #"/")
                   last
                   (str/split #"\?")
                   first)]
    {:dbtype "postgresql"
     :dbname dbname
     :user user
     :password password
     :host host
     :port (Integer. port)}))

(def db-spec (parse-database-url database-url))

(defprotocol DbConn
  (query [this sql-map])
  (execute! [this sql-map]))

(defrecord DbConnImpl [db-spec]
  DbConn
  (query
   [_this sql-map]
   (let [sql (sql/format sql-map)]
     (jdbc/query db-spec sql)))
  (execute!
   [_this sql-map]
   (let [sql (sql/format sql-map)]
     (jdbc/execute! db-spec sql))
   nil))

(defrecord WithLog [conn]
  DbConn
  (query
   [_this sql-map]
   (println "Querying SQL:" (pr-str sql-map))
   (time (query conn sql-map)))
  (execute!
   [_this sql-map]
   (println "Executing SQL:" (pr-str sql-map))
   (time (execute! conn sql-map))
   nil))

(def conn (-> db-spec ->DbConnImpl ->WithLog))

(comment
  (query conn {:select [:user_id :session_id :created_at_posix]
               :from :user_session
               :limit 5}))
