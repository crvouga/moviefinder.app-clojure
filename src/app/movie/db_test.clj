(ns app.movie.db-test
  (:require [clojure.test :refer [deftest testing is]]))

(deftest movie-db-test
  (testing "find movies"
    (is (= 1 1))))
