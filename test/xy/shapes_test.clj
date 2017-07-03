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
          holes [[[1.5 2.5] [2.5 3.5] [4.5 5.5] [1.5 2.5]]]
          poly-str "POLYGON ((1 2, 3 4, 5 6, 1 2), (1.5 2.5, 2.5 3.5, 4.5 5.5, 1.5 2.5))"]
      (is (= (str (shapes/polygon outer-ring holes)) poly-str))
      (is (= (str (shapes/polygon [outer-ring holes])) poly-str)))))

(deftest multipolygon-test
  (testing "MULTIPOLYGON"
    (let [outer-ring [[1 2] [3 4] [5 6] [1 2]]
          holes [[[1.5 2.5] [2.5 3.5] [4.5 5.5] [1.5 2.5]]]
          outer-ring-two [[4 5] [6 7] [8 9] [4 5]]
          holes-two [[[4.5 5.5] [5.5 6.5] [7.5 8.5] [4.5 5.5]]]
          mp-str "MULTIPOLYGON (((1 2, 3 4, 5 6, 1 2), (1.5 2.5, 2.5 3.5, 4.5 5.5, 1.5 2.5)), ((4 5, 6 7, 8 9, 4 5), (4.5 5.5, 5.5 6.5, 7.5 8.5, 4.5 5.5)))"]
      (is (= (str (shapes/multi-polygon [[outer-ring holes] [outer-ring-two holes-two]]))
             mp-str)))))


(deftest within-test
  (testing "WITHIN"
    (let [pt (shapes/point 1 2)
          poly (shapes/polygon [[[0 0] [3 0] [3 3] [3 0] [0 0]]])]
      (is (shapes/within pt poly)))))