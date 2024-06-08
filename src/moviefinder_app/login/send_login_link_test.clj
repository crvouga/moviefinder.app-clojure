(ns moviefinder-app.login.send-login-link-test
  (:require [clojure.string :refer [includes?]]
            [clojure.test :refer [deftest is testing]]
            [moviefinder-app.email.send-email :as send-email]
            [moviefinder-app.email.send-email-impl]
            [moviefinder-app.login.login-link-db :as login-link-db]
            [moviefinder-app.login.login-link-db-impl]
            [moviefinder-app.route :as route]
            [moviefinder-app.login.fixture :refer [fixture]]
            [moviefinder-app.login.send-login-link :refer [send-login-link! ->login-link-route]]))

(deftest send-login-link-test
  (testing "send login link"
    (let [f (fixture)
          login-link-db (f :login-link-db/login-link-db)
          email (:login/email f)
          before (login-link-db/find-by-email! login-link-db email)
          _ (send-login-link! f)
          after (login-link-db/find-by-email! login-link-db email)]

      (is (= before #{}))
      (is (= (count after) 1))))

  (testing "it should send an email"
    (let [f (fixture)
          send-email (:send-email/send-email f)
          before (send-email/get-sent-emails! send-email)
          _ (send-login-link! f)
          after (send-email/get-sent-emails! send-email)]
      (is (= before #{}))
      (is (= (count after) 1))))

  (testing "login email should include login link"
    (let [f (fixture)
          login-link (send-login-link! f)
          sent (first (send-email/get-sent-emails! (:send-email/send-email f)))
          login-link-url (-> login-link ->login-link-route route/encode)]
      (is (includes? (sent :email/body-html) login-link-url)))))