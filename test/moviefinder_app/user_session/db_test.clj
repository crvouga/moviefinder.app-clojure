(ns moviefinder-app.user-session.db-test
  (:require [clojure.test :refer [deftest testing is]]
            [moviefinder-app.user-session.db :as user-session-db]
            [moviefinder-app.user-session.db-impl]))

(defn fixture []
  (let [db (user-session-db/->UserSessionDb {:user-session-db/impl 
                                             :user-session-db/impl-in-memory})] 
    {:fixture/db db}))

(deftest user-session-db-test
  (testing "find user id by session id"
    (let [f (fixture)
          session {:session/id 1 :user/id 2}]
      (user-session-db/insert! (f :fixture/db) session)
      (is (= 2 (user-session-db/find-user-id-by-session-id! (f :fixture/db) 1)))
      (is (= 1 1))))

  (testing "insert user session"
    (is (= 1 1))))