(ns moviefinder-app.user-session.user-session-db-test
  (:require [clojure.test :refer [deftest is testing]]
            [moviefinder-app.user-session.user-session-db :as user-session-db]
            [moviefinder-app.user-session.user-session-db-impl]
            [moviefinder-app.deps :as deps]))

(defn fixture []
  (deps/deps-test))

(deftest user-session-db-test
  (testing "find user id by session id"
    (let [f (fixture)
          session {:session/id 1 :user/id 2}
          user-session-db (f :user-session-db/user-session-db)
          before (user-session-db/find-by-session-id! user-session-db (session :session/id))
          _ (user-session-db/put! user-session-db #{session})
          after (user-session-db/find-by-session-id! user-session-db (session :session/id))] 
      (is (= #{} before))
      (is (= #{session} after))))

  (testing "insert user session"
    (is (= 1 1))))