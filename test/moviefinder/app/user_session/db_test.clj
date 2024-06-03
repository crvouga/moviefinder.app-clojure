(ns moviefinder.app.user-session.db-test
  (:require [clojure.test :refer [deftest testing is]]
            [moviefinder.app.user-session.db :as user-session-db]
            [moviefinder.app.user-session.db-impl]))

(def db
  (user-session-db/->UserSessionDb
   {:user-session-db/impl :sql}))

(deftest user-session-db-test
  (testing "find user id by session id"
    (is (= 1 0)))

  (testing "insert user session"
    (is (= 1 1))))