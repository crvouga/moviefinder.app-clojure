(ns moviefinder-app.user-session.user-session-db-test
  (:require [clojure.test :refer [deftest is testing]]
            [moviefinder-app.user-session.user-session-db :as user-session-db]
            [moviefinder-app.user-session.user-session :as user-session]
            [moviefinder-app.user-session.user-session-db-impl]
            [moviefinder-app.deps :as deps]))

(defn fixture []
  (deps/deps-test-int))

(comment
  (def user-session-db
    (-> (deps/deps-test-int) :user-session-db/user-session-db))
  
  user-session-db

  (def user-session  (user-session/random!))
  user-session
  
  (user-session-db/put! user-session-db #{user-session})

  (user-session-db/find-by-session-id! user-session-db (user-session :session/id))

  (user-session-db/zap-by-session-id! user-session-db (user-session :session/id)))

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
  
  (testing "find by user id"
    (let [f (fixture)
          session (user-session/random!)
          user-session-db (f :user-session-db/user-session-db)
          before (user-session-db/find-by-user-id! user-session-db (session :user/id))
          _ (user-session-db/put! user-session-db #{session})
          after (user-session-db/find-by-user-id! user-session-db (session :user/id))]
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
  
  (testing "zapping by session id"
    (let [f (fixture)
          session (user-session/random!)
          user-session-db (f :user-session-db/user-session-db)
          _ (user-session-db/put! user-session-db #{session})
          before (user-session-db/find-by-session-id! user-session-db (session :session/id))
          _ (user-session-db/zap-by-session-id! user-session-db (session :session/id))
          after (user-session-db/find-by-session-id! user-session-db (session :session/id))]
      (is (= #{session} before))
      (is (= #{} after))))
  
  (testing "put! should not error on putting the same item twice"
    (let [f (fixture)
          session (user-session/random!)
          user-session-db (f :user-session-db/user-session-db)
          _ (user-session-db/put! user-session-db #{session})
          _ (user-session-db/put! user-session-db #{session})
          after (user-session-db/find-by-session-id! user-session-db (session :session/id))]
      (is (= #{session} after))))
  )