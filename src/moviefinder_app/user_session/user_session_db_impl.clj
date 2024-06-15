(ns moviefinder-app.user-session.user-session-db-impl
  (:require  [moviefinder-app.user-session.user-session-db :as user-session-db]
             [moviefinder-app.user-session.user-session-db-impl-in-memory]
             [moviefinder-app.user-session.user-session-db-impl-postgres]
             [moviefinder-app.user-session.user-session-db-impl-with-cache]))

(defn postgres [db-conn]
  (user-session-db/->UserSessionDb
   {:user-session-db/impl :user-session-db-impl/postgres
    :db/conn db-conn}))

(defn in-memory []
  (user-session-db/->UserSessionDb
   {:user-session-db/impl :user-session-db-impl/in-memory}))

(defn with-cache [source cache]
  (user-session-db/->UserSessionDb
   {:user-session-db/impl :user-session-db-impl/with-cache
    :with-cache/source source
    :with-cache/cache cache}))

(defn postgres-with-cache [db-conn]
  (with-cache (postgres db-conn) (in-memory)))