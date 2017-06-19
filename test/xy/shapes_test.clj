(ns xy.shapes-test
  (:require [clojure.test :refer :all]
            [xy.shapes :as shapes]))

(deftest point-test
  (testing "POINT"
    (is (= (str (shapes/point 1 2)) "POINT (1 2)"))))

(deftest multi-point-test
  (testing "MULTIPOINT"
    (is (= (str (shapes/multi-point [[1 2] [3 4]]))
           "MULTIPOINT ((1 2), (3 4))"))))

(deftest linear-ring-test
  (testing "LINEARRING"
    (is (= (str (shapes/linear-ring [[1 2] [2 3] [3 4] [1 2]]))
          "LINEARRING (1 2, 2 3, 3 4, 1 2)"))))

(deftest linestring-test
  (testing "LINESTRING"
    (is (= (str (shapes/linestring [[1 2] [3 4]]))
           "LINESTRING (1 2, 3 4)"))))

(deftest multi-linestring-test
  (testing "MULTILINESTRING"
      (is (= (str (shapes/multi-linestring [[[1 2] [3 4]] [[4 5] [6 7]]]))
           "MULTILINESTRING ((1 2, 3 4), (4 5, 6 7))"))))

(deftest polygon-test
  (testing "POLYGON"
    (let [outer-ring [[1 2] [3 4] [5 6] [1 2]]
          holes [[[1.5 2.5] [2.5 3.5] [4.5 5.5] [1.5 2.5]]]]
      (is (= (str (shapes/polygon outer-ring holes)))))))

