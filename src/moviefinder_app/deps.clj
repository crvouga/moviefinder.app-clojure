(ns moviefinder-app.deps
  (:require [moviefinder-app.db :as db]
            [moviefinder-app.email.send-email-impl :as send-email-impl]
            [moviefinder-app.env :as env]
            [moviefinder-app.login.login-with-email.login-link.login-link-db-impl :as login-link-db-impl]
            [moviefinder-app.login.login-with-sms.verify-sms.verify-sms-impl :as verify-sms-impl]
            [moviefinder-app.movie.movie-db-impl :as movie-db-impl]
            [moviefinder-app.user-session.user-session-db-impl :as user-session-db-impl]
            [moviefinder-app.user.user-db-impl :as user-db-impl]))

(defn deps-test-unit []
  {:movie-db/movie-db (movie-db-impl/tmdb)
   :user-db/user-db (user-db-impl/in-memory)
   :user-session-db/user-session-db (user-session-db-impl/in-memory)
   :login-link-db/login-link-db (login-link-db-impl/in-memory)
   :verify-sms/verify-sms (verify-sms-impl/mock)
   :send-email/send-email (send-email-impl/mock)})

(defn deps-test-int []
  (merge
   (deps-test-unit)
   {:user-session-db/user-session-db (user-session-db-impl/postgres-with-cache db/conn)}))

(defn int-test? []
  (boolean (env/get! "INTEGRATION_TEST" false)))

(defn deps-test []
  (if (int-test?)
    (deps-test-int)
    (deps-test-unit)))  

(defn deps-real []
  (merge
   (deps-test-int)
   {:verify-sms/verify-sms  (verify-sms-impl/twilio)
    :send-email/send-email (send-email-impl/mock {:send-email/log? true})}))

(def deps (deps-real))

(defn assoc-deps [request]
  (merge request deps))