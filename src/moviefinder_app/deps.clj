(ns moviefinder-app.deps
  (:require [moviefinder-app.email.send-email :as send-email]
            [moviefinder-app.email.send-email-impl]
            [moviefinder-app.login.login-link.login-link-db :as login-link-db]
            [moviefinder-app.login.login-link.login-link-db-impl]
            [moviefinder-app.user-session.user-session-db :as user-session-db]
            [moviefinder-app.user-session.user-session-db-impl]
            [moviefinder-app.user.user-db :as user-db]
            [moviefinder-app.user.user-db-impl]
            [moviefinder-app.movie.movie-db :as movie-db]
            [moviefinder-app.movie.movie-db-impl]
            [moviefinder-app.db :as db]
            [moviefinder-app.env :as env]))

(defn deps-test-unit []
  {:movie-db/movie-db
   (movie-db/->MovieDb
    {:movie-db/impl :movie-db-impl/tmdb})

   :user-db/user-db
   (user-db/->UserDb
    {:user-db/impl :user-db-impl/in-memory})

   :user-session-db/user-session-db
   (user-session-db/->UserSessionDb
    {:user-session-db/impl :user-session-db-impl/in-memory})

   :login-link-db/login-link-db
   (login-link-db/->LoginLinkDb
    {:login-link-db/impl :login-link-db-impl/in-memory})

   :send-email/send-email
   (send-email/->SendEmail
    {:send-email/impl :send-email-impl/mock
     :send-email/log? false})})

(defn deps-test-int []
  (merge
   (deps-test-unit)
   {:user-session-db/user-session-db
    (user-session-db/->UserSessionDb
     {:user-session-db/impl :user-session-db-impl/postgres
      :db/conn db/conn})}))

(defn int-test? []
  (boolean (env/get! "INTEGRATION_TEST" false)))

(defn deps-test []
  (if (int-test?)
    (deps-test-int)
    (deps-test-unit)))  

(defn deps-real []
  (merge
   (deps-test-int)
   {:send-email/send-email
    (send-email/->SendEmail
     {:send-email/impl :send-email-impl/mock
      :send-email/log? true})}))


(def deps (deps-real))

(defn assoc-deps [request]
  (merge request deps))