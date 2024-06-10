(ns moviefinder-app.user-session.user-session-db-test
  (:require [clojure.test :refer [deftest is testing]]
            [moviefinder-app.user-session.user-session-db :as user-session-db]
            [moviefinder-app.user-session.user-session :as user-session]
            [moviefinder-app.user-session.user-session-db-impl]
            [moviefinder-app.deps :as deps]))

(defn fixture []
  (deps/deps-test))

(deftest user-session-db-test
  (testing "put and find"
    (let [f (fixture)
          session (user-session/random!)
          user-session-db (f :user-session-db/user-session-db)
          before (user-session-db/find-by-session-id! user-session-db (session :session/id))
          _ (user-session-db/put! user-session-db #{session})
          after (user-session-db/find-by-session-id! user-session-db (session :session/id))]
      (is (= #{} before))
      (is (= #{session} after))))
  
  (testing "assoc timestamps if missing"
    (let [f (fixture)
          session (-> (user-session/random!) (dissoc :user-session/created-at-posix))
          user-session-db (f :user-session-db/user-session-db)
          _ (user-session-db/put! user-session-db #{session})
          after (first (user-session-db/find-by-session-id! user-session-db (session :session/id)))]
      (is (not (contains? session :user-session/created-at-posix)))
      (is (contains? after :user-session/created-at-posix))))
  
  )