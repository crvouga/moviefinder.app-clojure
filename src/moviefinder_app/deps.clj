(ns moviefinder-app.deps
  (:require [moviefinder-app.email.send-email :as send-email]
            [moviefinder-app.email.send-email-impl]
            [moviefinder-app.login.login-link-db :as login-link-db]
            [moviefinder-app.login.login-link-db-impl]
            [moviefinder-app.user-session.user-session-db :as user-session-db]
            [moviefinder-app.user-session.user-session-db-impl]
            [moviefinder-app.user.user-db :as user-db]
            [moviefinder-app.user.user-db-impl]
            [moviefinder-app.movie.movie-db :as movie-db]
            [moviefinder-app.movie.movie-db-impl]))

(defn deps-test []
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

(defn deps-real []
  (merge
   (deps-test)
   {:send-email/send-email
    (send-email/->SendEmail
     {:send-email/impl :send-email-impl/mock
      :send-email/log? true})}))
