(ns moviefinder-app.user.user-db-test
  (:require [clojure.test :refer [deftest is testing]]
            [moviefinder-app.user.user :as user]
            [moviefinder-app.user.user-db :as user-db]
            [moviefinder-app.deps :as deps]))

(defn fixture []
  (deps/deps-test))

(deftest user-db-test
  (testing "find by email"
    (let [f (fixture)
          user (user/random!)
          before (user-db/find-by-email! (:user-db/user-db f) (:user/email user))
          _ (user-db/put! (:user-db/user-db f) #{user})
          after (user-db/find-by-email! (:user-db/user-db f) (:user/email user))]
      (is (= before #{}))
      (is (= after #{user}))))
  
  (testing "find by phone number"
    (let [f (fixture)
          user (user/random!)
          before (user-db/find-by-phone-number! (:user-db/user-db f) (:user/phone-number user))
          _ (user-db/put! (:user-db/user-db f) #{user})
          after (user-db/find-by-phone-number! (:user-db/user-db f) (:user/phone-number user))]
      (is (= before #{}))
      (is (= after #{user}))))
  
  
  (testing "find by id"
    (let [f (fixture)
          user (user/random!)
          before (user-db/find-by-id! (:user-db/user-db f) (:user/id user))
          _ (user-db/put! (:user-db/user-db f) #{user})
          after (user-db/find-by-id! (:user-db/user-db f) (:user/id user))]
      (is (= before #{}))
      (is (= after #{user})))))



