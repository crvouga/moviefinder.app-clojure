(ns moviefinder-app.login.login-with-email.login-link.login-link-db-test
  (:require [clojure.set :as set]
            [clojure.test :refer [deftest is testing]]
            [moviefinder-app.login.login-with-email.login-link.login-link :as login-link]
            [moviefinder-app.login.login-with-email.login-link.login-link-db :as login-link-db]
            [moviefinder-app.login.login-with-email.login-link.login-link-db-impl]))


(defn fixture []
  {::db (login-link-db/->LoginLinkDb
         {:login-link-db/impl :login-link-db-impl/in-memory})})



(deftest login-link-db-test
  (testing "find and insert"
    (let [f (fixture)
          db (f ::db)
          login-link (login-link/random!)
          email (-> login-link :login-link/email)
          before (login-link-db/find-by-email! db email)
          _ (login-link-db/put! db #{login-link})
          after (login-link-db/find-by-email! db email)]
      (is (= before #{}))
      (is (= after #{login-link}))))
  

  (testing "find by id"
    (let [f (fixture)
          db (f ::db)
          login-link (login-link/random!)
          id (-> login-link :login-link/id)
          before (login-link-db/find-by-id! db id)
          _ (login-link-db/put! db #{login-link})
          after (login-link-db/find-by-id! db id)]
      (is (= before #{}))
      (is (= after #{login-link}))))
  
  (testing "it should be unique by id"
    (let [f (fixture)
           db (f ::db)
           login-link (login-link/random!)
           id (-> login-link :login-link/id)
           before (login-link-db/find-by-id! db id)
            _ (doseq [_ (range 3)]
                (login-link-db/put! db #{login-link}))
           after (login-link-db/find-by-id! db id)]
       (is (= before #{}))
       (is (= after #{login-link}))))
  

  (testing "it should always keep the last one put"
    (let [f (fixture)
          db (f ::db)
          login-link (login-link/random!)
          _ (login-link-db/put! db #{login-link})
          before (login-link-db/find-by-id! db (-> login-link :login-link/id))
          used-login-link (login-link/mark-as-used login-link)
          _ (login-link-db/put! db #{used-login-link})
          after (login-link-db/find-by-id! db (-> login-link :login-link/id))]
      (is (= before #{login-link}))
      (is (= after #{used-login-link}))))

  (testing "find and insert with many"
    (let [f (fixture)
          db (f ::db)
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