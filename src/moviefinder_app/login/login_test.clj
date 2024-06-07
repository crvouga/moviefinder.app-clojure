(ns moviefinder-app.login.login-test
  (:require [clojure.string :refer [includes?]]
            [clojure.test :refer [deftest is testing]]
            [moviefinder-app.error :refer [thrown-err?]]
            [moviefinder-app.email.send-email :as send-email]
            [moviefinder-app.email.send-email-impl]
            [moviefinder-app.login.login-link-db :as login-link-db]
            [moviefinder-app.login.login-link-db-impl]
            [moviefinder-app.login.login-link :as login-link]
            [moviefinder-app.route :as route]
            [moviefinder-app.user-session.user-session-db :as user-session-db]
            [moviefinder-app.user-session.user-session-db-impl]
            [moviefinder-app.user.user-db :as user-db]
            [moviefinder-app.user.user-db-impl]
            [moviefinder-app.user.user :as user]
            [moviefinder-app.login.login :as login]))

(defn fixture []
  (let [login-link-db (login-link-db/->LoginLinkDb {:login-link-db/impl :login-link-db/impl-in-memory})
        user-session-db (user-session-db/->UserSessionDb {:user-session-db/impl :user-session-db/impl-in-memory})
        send-email (send-email/->SendEmail {:send-email/impl :send-email/impl-mock :send-email/log? false})
        user-db (user-db/->UserDb {:user-db/impl :user-db/impl-in-memory})]
    {:login/email "test@test.com"
     :user-session/id "test-user-session-id"
     :login-link-db/login-link-db login-link-db
     :send-email/send-email send-email
     :user-session-db/user-session-db user-session-db
     :user-db/user-db user-db}))

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
          login-link-url (-> login-link login/->login-link-route route/encode)]
      (is (includes? (sent :email/body-html) login-link-url))))


  (testing "use login link"
    (let [f (fixture)
          login-link (login/send-login-with-email-link! f)
          _ (login/use-login-link! (merge f login-link))
          after (first (login-link-db/find-by-email! (f :login-link-db/login-link-db) (:login/email f)))]
      (is (not (nil? (after :login-link/used-at-posix))))))


  (testing "user should be logged in after using login link"
    (let [f (fixture)
          login-link (login/send-login-with-email-link! f)
          before (user-session-db/find-by-session-id!
                  (f :user-session-db/user-session-db)
                  (f :user-session/id))
          _ (login/use-login-link! (merge f login-link))
          after (user-session-db/find-by-session-id!
                 (f :user-session-db/user-session-db)
                 (f :user-session/id))]
      (is (= before #{}))
      (is (= (count after) 1))))

  (testing "it should create a new user if the email is not found"
    (let [f (fixture)
          login-link (login/send-login-with-email-link! f)
          before (first (user-db/find-by-email! (f :user-db/user-db) (:login/email f)))
          _ (login/use-login-link! (merge f login-link))
          after (first (user-db/find-by-email! (f :user-db/user-db) (:login/email f)))]
      (is (nil? before))
      (is (not (nil? after)))))

  (testing "it should NOT create a new user if already exists"
    (let [f (fixture)
          login-link (login/send-login-with-email-link! f)
          user (user/new! (f :login/email))
          _ (user-db/put! (f :user-db/user-db) #{user})
          before (user-db/find-by-email! (f :user-db/user-db) (:login/email f))
          _ (login/use-login-link! (merge f login-link))
          after (user-db/find-by-email! (f :user-db/user-db) (:login/email f))]
      (is (= before after))
      (is (= after #{user}))))

  (testing "it should error if the login link is already used"
    (let [f (fixture)
          login-link (login/send-login-with-email-link! f)
          input (merge f login-link)
          _ (login/use-login-link! input)
          _ (thrown-err? :err/login-link-already-used (login/use-login-link! input))]))

  (testing "it should error if the login link does not exist"
    (let [f (fixture)
          login-link-new (login-link/new! (user/random!))
          input (merge f login-link-new)
          _ (thrown-err? :err/login-link-not-found (login/use-login-link! input))]))

  (testing "it should error if login link is expired"
    (let [f (fixture)
          login-link (login/send-login-with-email-link! f)
          login-link-expired (login-link/mark-as-expired login-link)
          _ (login-link-db/put! (f :login-link-db/login-link-db) #{login-link-expired})
          input (merge f login-link-expired)
          _ (thrown-err? :err/login-link-expired (login/use-login-link! input))])))
      
