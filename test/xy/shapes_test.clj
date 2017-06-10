(ns xy.shapes-test
  (:require [clojure.test :refer :all]
            [xy.shapes :as shapes]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))

(deftest point-test
  (testing "POINT"
    (is (= (str (shapes/point 1 2)) "POINT (1 2)"))))

(deftest multi-point-test
  (testing "MULTIPOINT"
    (is (= (str (shapes/multi-point (list (shapes/point  1 2) (shapes/point 3 4))))
           "MULTIPOINT ((1 2), (3 4))"))))
