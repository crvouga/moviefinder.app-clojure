(ns moviefinder-app.login.login-link-db-test
  (:require [clojure.test :refer [deftest testing is]]
            [moviefinder-app.login.login-link-db :as login-link-db]
            [moviefinder-app.login.login-link-db-impl]
            [moviefinder-app.login.login-link :as login-link]
            [clojure.set :as set]))


(defn fixture []
  {:f/db (login-link-db/->LoginLinkDb
          {:login-link-db/impl :login-link-db/impl-in-memory})})



(deftest login-link-db-test
  (testing "find and insert"
    (let [f (fixture)
          db (f :f/db)
          login-link (login-link/random!)
          email (-> login-link :login-link/email)
          before (login-link-db/find-by-email! db email)
          _ (login-link-db/put! db #{login-link})
          after (login-link-db/find-by-email! db email)]
      (is (= before #{}))
      (is (= after #{login-link}))))

  (testing "find and insert with many"
    (let [f (fixture)
          db (f :f/db)
          login-link-a (login-link/random!)
          login-link-b (login-link/random!)
          login-link-c (login-link/random!)

          login-links #{login-link-a login-link-b login-link-c}

          before (->> login-links
                      (map :login-link/email)
                      (map #(login-link-db/find-by-email! db %))
                      (apply set/union))

          _ (login-link-db/put! db login-links)

          after (->> login-links
                     (map :login-link/email)
                     (map #(login-link-db/find-by-email! db %))
                     (apply set/union))] 
      
      (is (= before #{}))
      (is (= login-links after)))))