(ns xy.geojson-test
  (:require [clojure.test :refer :all]
            [xy.shapes :as shapes]
            [xy.geojson :as gj]))

(deftest write-geojson
  (testing "GeoJSON writing"
    (let [poly (gj/write
                 (shapes/polygon
                   [[[1 1] [1 2] [2 2] [2 1] [1 1]]
                    [[[1.4 1.4] [1.4 1.6] [1.6 1.6] [1.6 1.4] [1.4 1.4]]]]))]
      (is (= poly "{\"type\":\"Polygon\",\"coordinates\":[[[1.0,1.0],[1.0,2.0],[2.0,2.0],[2.0,1.0],[1.0,1.0]],[[[1.4,1.4],[1.4,1.6],[1.6,1.6],[1.6,1.4],[1.4,1.4]]]]}")))))
