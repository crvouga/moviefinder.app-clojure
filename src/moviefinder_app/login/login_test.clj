(ns moviefinder-app.login.login-test
  (:require [clojure.string :refer [includes?]]
            [clojure.test :refer [deftest is testing]]
            [moviefinder-app.email.send-email :as send-email]
            [moviefinder-app.email.send-email-impl]
            [moviefinder-app.login.login :as login]
            [moviefinder-app.login.login-link-db :as login-link-db]
            [moviefinder-app.login.login-link-db-impl]
            [moviefinder-app.route]))

(defn fixture []
  (let [login-link-db (login-link-db/->LoginLinkDb
            {:login-link-db/impl :login-link-db/impl-in-memory})
        send-email (send-email/->SendEmail
                     {:send-email/impl :send-email/impl-mock
                      :send-email/log? false})]
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
      (is (= (count after) 1))))
  
  (testing "login email should include login link"
    (let [f (fixture)
          login-link (login/send-login-with-email-link! f)
          sent (first (send-email/get-sent-emails! (:send-email/send-email f)))
          login-link-url (-> login-link login/->login-link-route moviefinder-app.route/encode)]
      (is (includes? (sent :email/body-html) login-link-url))))
  
  
  (testing "use login link"
    (let [f (fixture)
          login-link (login/send-login-with-email-link! f)
          _ (login/use-login-link! (merge f login-link))
          after (first (login-link-db/find-by-email! (f :login-link-db/login-link-db) (:login/email f)))]
      (is (not (nil? (after :login-link/used-at-posix)))))))