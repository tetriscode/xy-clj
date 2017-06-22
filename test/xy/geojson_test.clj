(ns xy.geojson-test
  (:require [clojure.test :refer :all]
            [xy.shapes :as shapes]))

(deftest parse-geojson
  (testing "GeoJSON parsing"
    (let [point (shapes/point 1 2)]
      (is (= 1 1)))))

(deftest write-geojson
  (testing "GeoJSON writing"
    (is (= 1 1))))