(ns app.movie.db.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [app.movie.db.impl-tmdb :refer [->MoveDbTmdb]]))

(def movie-db (->MoveDbTmdb))

(deftest movie-db-test
  (testing "find movies"
    (is (= 1 1))))
