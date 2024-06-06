(ns moviefinder-app.login-test
  (:require [clojure.test :refer [deftest testing is]]
            [moviefinder-app.login :as login]
            [moviefinder-app.login.login-link-db :as login-link-db]
            [moviefinder-app.login.login-link-db-impl]
            [moviefinder-app.email.send-email :as send-email]
            [moviefinder-app.email.send-email-impl]))

(defn fixture []
  (let [login-link-db (login-link-db/->LoginLinkDb
            {:login-link-db/impl :login-link-db/impl-in-memory})
        send-email (send-email/->SendEmail
                     {:send-email/impl :send-email/impl-mock})]
    {:login/email "test@test.com"
     :login/session-id "1234"
     :login-link-db/login-link-db login-link-db
     :send-email/send-email send-email}))

(deftest login-test
  (testing "send login link"
    (let [f (fixture)
          login-link-db (f :login-link-db/login-link-db)
          email (:login/email f)
          before (login-link-db/find-by-email! login-link-db email)
          _ (login/send-login-with-email-link! f)
          after (login-link-db/find-by-email! login-link-db email)]

      (is (= before #{}))
      (is (= (count after) 1))))
  
  (testing "it should send an email"
    (let [f (fixture)
          send-email (:send-email/send-email f)
          before (send-email/get-sent-emails! send-email)
          _ (login/send-login-with-email-link! f)
          after (send-email/get-sent-emails! send-email)]
      (is (= before #{}))
      (is (= (count after) 1)))))