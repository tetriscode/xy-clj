(ns xy.shapes-test
  (:require [clojure.test :refer :all]
            [xy.shapes :as shapes]))

(deftest point-test
  (testing "POINT"
    (is (= (str (shapes/point 1 2)) "POINT (1 2)"))))

(deftest multi-point-test
  (testing "MULTIPOINT"
    (is (= (str (shapes/multi-point (list (shapes/point  1 2) (shapes/point 3 4))))
           "MULTIPOINT ((1 2), (3 4))"))))

(deftest linear-ring-test
  (testing "LINEARRING"
    (is (= (str (shapes/linear-ring (list (shapes/coordinate 1 2)
                                          (shapes/coordinate 2 3)
                                          (shapes/coordinate 3 4)
                                          (shapes/coordinate 1 2))))
          "LINEARRING (1 2, 2 3, 3 4, 1 2)"))))

(deftest linestring-test
  (testing "LINESTRING"
    (is (= (str (shapes/linestring (list (shapes/coordinate 1 2)
                                         (shapes/coordinate 3 4))))
           "LINESTRING (1 2, 3 4)"))))

(deftest multi-linestring-test
  (testing "MULTILINESTRING"
    (let [linestrings  (list (shapes/linestring (list (shapes/coordinate 1 2) (shapes/coordinate 3 4)))
                             (shapes/linestring (list  (shapes/coordinate 4 5) (shapes/coordinate 6 7))))]
      (is (= (str (shapes/multi-linestring linestrings))
           "MULTILINESTRING ((1 2, 3 4), (4 5, 6 7))")))))

(defn list->coords [coords]
  (map (fn [[x y]] (shapes/coordinate x y)) coords))

(deftest polygon-test
  (testing "POLYGON"
    (let [outer-ring (list->coords [[1 2] [3 4] [5 6] [1 2]])
          holes (list->coords [[1.5 2.5] [2.5 3.5] [4.5 5.5]])]
      (is (= (str (shapes/polygon outer-ring holes)))))))

