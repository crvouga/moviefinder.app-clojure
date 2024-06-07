(ns moviefinder-app.user-session.user-session-db-test
  (:require [clojure.test :refer [deftest is testing]]
            [moviefinder-app.user-session.user-session-db :as user-session-db]
            [moviefinder-app.user-session.user-session-db-impl]))

(defn fixture []
  (let [db (user-session-db/->UserSessionDb
            {:user-session-db/impl
             :user-session-db/impl-in-memory})] 
    {:f/db db}))

(deftest user-session-db-test
  (testing "find user id by session id"
    (let [f (fixture)
          session {:user-session/id 1 :user/id 2}]
      (user-session-db/put! (f :f/db) #{session})
      (is (= #{session} (user-session-db/find-by-session-id! (f :f/db) (session :user-session/id))))))

  (testing "insert user session"
    (is (= 1 1))))