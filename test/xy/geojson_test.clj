(ns xy.geojson-test
  (:require [clojure.test :refer :all]
            [xy.shapes :as shapes]
            [xy.geojson :as gj]))

(defn load-file [fname]
  (-> (slurp (str "devresources/" fname))
      (clojure.string/replace " " "")
      (clojure.string/replace "\n" "")))

(def linestring-str (load-file "linestring.geojson"))
(def point-str (load-file "point.geojson"))
(def polygon-str (load-file "polygon.geojson"))
(def multipoint-str (load-file "multipoint.geojson"))
(def multilinestring-str (load-file "multilinestring.geojson"))
(def multipolygon-str (load-file "multipolygon.geojson"))
(def feature (load-file "feature.geojson"))
(def featurecollection (load-file "featurecollection.geojson"))

(deftest point-test
  (testing "Point string IO"
    (let [pt (gj/parse-str point-str)
          pt-gj (gj/write pt)]
      (is (some? pt))
      (is (= "Point" (.getGeometryType pt)))
      (is (= pt-gj point-str)))))

(deftest polygon-test
  (testing "Polygon string IO"
    (let [poly (gj/parse-str polygon-str)
          poly-gj (gj/write poly)]
      (is (some? poly))
      (is (= "Polygon" (.getGeometryType poly)))
      (is (= poly-gj polygon-str)))))

(deftest linestring-test
  (testing "Linestring string IO"
    (let [ls (gj/parse-str linestring-str)
          linestring-gj (gj/write ls)]
      (is (some? ls))
      (is (= "LineString" (.getGeometryType ls)))
      (is (= linestring-gj linestring-str)))))


