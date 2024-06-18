(ns moviefinder-app.media.media-db-test
  (:require [clojure.test :refer [deftest testing]]
            [moviefinder-app.deps :as deps]
            [moviefinder-app.media.media-db-impl :as media-db-impl]))

(defn fixture []
  (-> (deps/deps-test)
      (assoc :media-db/media-db (media-db-impl/in-memory))))


(comment
  (def f (fixture))
  
  )

(deftest media-db-test
  (testing "putting and finding"))