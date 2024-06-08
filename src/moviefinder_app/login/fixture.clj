(ns moviefinder-app.login.fixture
  (:require [moviefinder-app.email.send-email :as send-email]
            [moviefinder-app.email.send-email-impl]
            [moviefinder-app.login.login-link-db :as login-link-db]
            [moviefinder-app.login.login-link-db-impl]
            [moviefinder-app.user-session.user-session-db-interface :as user-session-db]
            [moviefinder-app.user-session.user-session-db-impl]
            [moviefinder-app.user.user-db :as user-db]
            [moviefinder-app.user.user-db-impl]))

(defn fixture []
  (let [login-link-db (login-link-db/->LoginLinkDb {:login-link-db/impl :login-link-db-impl/in-memory})
        user-session-db (user-session-db/->UserSessionDb {:user-session-db/impl :user-session-db-impl/in-memory})
        send-email (send-email/->SendEmail {:send-email/impl :send-email-impl/mock :send-email/log? false})
        user-db (user-db/->UserDb {:user-db/impl :user-db-impl/in-memory})]
    {:login/email "test@test.com"
     :user-session/id "test-user-session-id"
     :login-link-db/login-link-db login-link-db
     :send-email/send-email send-email
     :user-session-db/user-session-db user-session-db
     :user-db/user-db user-db}))
