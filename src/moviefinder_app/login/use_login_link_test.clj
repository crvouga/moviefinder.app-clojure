(ns moviefinder-app.login.use-login-link-test
  (:require [clojure.test :refer [deftest is testing]]
            [moviefinder-app.error :refer [thrown-err?]]
            [moviefinder-app.login.login-link-db :as login-link-db]
            [moviefinder-app.login.login-link :as login-link]
            [moviefinder-app.user-session.user-session-db :as user-session-db]
            [moviefinder-app.user.user-db :as user-db]
            [moviefinder-app.user.user :as user]
            [moviefinder-app.login.fixture :refer [fixture]]
            [moviefinder-app.login.send-login-link :refer [send-login-link!]]
            [moviefinder-app.login.use-login-link :refer [use-login-link!]]))


(deftest use-login-link-test
  (testing "use login link"
    (let [f (fixture)
          login-link (send-login-link! f)
          _ (use-login-link! (merge f login-link))
          after (first (login-link-db/find-by-email! (f :login-link-db/login-link-db) (:login/email f)))]
      (is (not (nil? (after :login-link/used-at-posix))))))


  (testing "user should be logged in after using login link"
    (let [f (fixture)
          login-link (send-login-link! f)
          before (user-session-db/find-by-session-id!
                  (f :user-session-db/user-session-db)
                  (f :user-session/id))
          _ (use-login-link! (merge f login-link))
          after (user-session-db/find-by-session-id!
                 (f :user-session-db/user-session-db)
                 (f :user-session/id))]
      (is (= before #{}))
      (is (= (count after) 1))))

  (testing "it should create a new user if the email is not found"
    (let [f (fixture)
          login-link (send-login-link! f)
          before (first (user-db/find-by-email! (f :user-db/user-db) (:login/email f)))
          _ (use-login-link! (merge f login-link))
          after (first (user-db/find-by-email! (f :user-db/user-db) (:login/email f)))]
      (is (nil? before))
      (is (not (nil? after)))))

  (testing "it should NOT create a new user if already exists"
    (let [f (fixture)
          login-link (send-login-link! f)
          user (user/new! (f :login/email))
          _ (user-db/put! (f :user-db/user-db) #{user})
          before (user-db/find-by-email! (f :user-db/user-db) (:login/email f))
          _ (use-login-link! (merge f login-link))
          after (user-db/find-by-email! (f :user-db/user-db) (:login/email f))]
      (is (= before after))
      (is (= after #{user}))))

  (testing "it should error if the login link is already used"
    (let [f (fixture)
          login-link (send-login-link! f)
          input (merge f login-link)
          _ (use-login-link! input)
          _ (thrown-err? :use-login-link/login-link-already-used (use-login-link! input))]))

  (testing "it should error if the login link does not exist"
    (let [f (fixture)
          login-link-new (login-link/new! (user/random!))
          input (merge f login-link-new)
          _ (thrown-err? :use-login-link/login-link-not-found (use-login-link! input))]))

  (testing "it should error if login link is expired"
    (let [f (fixture)
          login-link (send-login-link! f)
          login-link-expired (login-link/mark-as-expired login-link)
          _ (login-link-db/put! (f :login-link-db/login-link-db) #{login-link-expired})
          input (merge f login-link-expired)
          _ (thrown-err? :use-login-link/login-link-expired (use-login-link! input))])))
      
