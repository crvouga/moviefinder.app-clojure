(ns moviefinder-app.user-session.user-session-db-impl-postgres
  (:require [moviefinder-app.user-session.user-session-db :as user-session-db]
            [moviefinder-app.db :as db]
            [moviefinder-app.user-session.user-session :as user-session]
            [honey.sql :as sql]
            [clojure.set :refer [rename-keys]]
            [moviefinder-app.user.user :as user]
            [moviefinder-app.session :as session]))

(defn- ensure-no-missing-columns [user-session]
  (merge {:user-session/created-at-posix (System/currentTimeMillis)
          :user-session/id (user-session/random-user-session-id!)} user-session))

(defn user-session->row [user-session]
  (-> user-session
      ensure-no-missing-columns
      (rename-keys
       {:session/id :session_id
        :user/id :user_id
        :user-session/created-at-posix :created_at_posix
        :user-session/id :user-session-id})))

(defn row->user-session [row]
  (rename-keys
   row
   {:session_id :session/id
    :user_id :user/id
    :user-session-id :user-session/id
    :created_at_posix :user-session/created-at-posix}))


(comment
  (def user-session (user-session/random!))
  user-session
  (def row (user-session->row user-session))
  row
  (def user-session-again (row->user-session row))
  user-session-again)

(defn- query-put [user-sessions]
  {:insert-into :user-session
   :values (->> user-sessions seq (map user-session->row))})

(comment
  (def user-sessions (set (for [_ (range 3)] (user-session/random!))))
  user-sessions
  (query-put user-sessions)
  (sql/format (query-put user-sessions))
  )

(defn- query-find-by-session-id [session-id]
  {:select [:user_id :session_id :created_at_posix :user_session_id]
   :from :user-session
   :where [:and 
           [:= :session_id (str session-id)]
           [:is :deleted_at_posix nil]]})

(comment
  (def session-id (session/random-session-id!))
  session-id
  (def query (query-find-by-session-id session-id))
  query
  (def sql (sql/format query))
  sql)

(defn- query-find-by-user-id [user-id]
  {:select [:user_id :session_id :created_at_posix :user_session_id]
   :from :user-session
   :where [:and
           [:= :user_id (str user-id)]
           [:is :deleted_at_posix nil]]})

(comment
  (def user-id (user/random-user-id!))
  user-id
  (def query (query-find-by-user-id user-id))
  query
  (def sql (sql/format query))
  sql
  )

(defn- query-zap-by-session-id [session-id]
  {:update :user-session
   :set {:deleted_at_posix (System/currentTimeMillis)}
   :where [:= :session_id (str session-id)]})

(comment
  (def session-id (session/random-session-id!))
  session-id
  (def query (query-zap-by-session-id session-id))
  query
  (def sql (sql/format query))
  sql
  )

(defrecord UserSessionDbPostgres [input]
  user-session-db/UserSessionDb
  (find-by-session-id!
    [_this session-id]
    (->> session-id
         (query-find-by-session-id)
         (db/query (:db/conn input))
         (map row->user-session)
         set))
  
  (find-by-user-id!
   [_this user-id]
   (->> user-id
        (query-find-by-user-id)
        (db/query (:db/conn input))
        (map row->user-session)
        set))

  (zap-by-session-id!
    [_this session-id]
    (->> session-id
        (query-zap-by-session-id)
        (db/execute! (:db/conn input))))

   (put!
    [_this user-sessions]
    (db/execute! (:db/conn input) (query-put user-sessions))
    nil))


(defmethod user-session-db/->UserSessionDb :user-session-db-impl/postgres [input]
  (->UserSessionDbPostgres input))

(comment
  (def user-session-db
    (user-session-db/->UserSessionDb
     {:user-session-db/impl :user-session-db-impl/postgres
      :db/conn db/conn}))

  (def user-session (user-session/random!))
  (user-session-db/put! user-session-db #{user-session})
  (user-session-db/find-by-session-id! user-session-db (user-session :session/id))

  (def user-sessions-many (set (for [_ (range 3)] (user-session/random!))))

  (user-session-db/put! user-session-db user-sessions-many)

  (def rand-user-session (-> user-sessions-many seq rand-nth))
  (user-session-db/find-by-session-id! user-session-db (rand-user-session :session/id))
  
  )