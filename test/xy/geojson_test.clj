(ns xy.geojson-test
  (:require [clojure.test :refer :all]
            [xy.shapes :as shapes]
            [xy.geojson :as gj]))

(defn load-geojson [fname]
  (-> (slurp (str "devresources/" fname))
      (clojure.string/replace " " "")
      (clojure.string/replace "\n" "")))

(def linestring-str (load-geojson "linestring.geojson"))
(def point-str (load-geojson "point.geojson"))
(def polygon-str (load-geojson "polygon.geojson"))
(def multipoint-str (load-geojson "multipoint.geojson"))
(def multilinestring-str (load-geojson "multilinestring.geojson"))
(def multipolygon-str (load-geojson "multipolygon.geojson"))
(def feature (load-geojson "feature.geojson"))
(def featurecollection (load-geojson "featurecollection.geojson"))

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
      (is (= polygon-str poly-gj)))))

(deftest linestring-test
  (testing "Linestring string IO"
    (let [ls (gj/parse-str linestring-str)
          linestring-gj (gj/write ls)]
      (is (some? ls))
      (is (= "LineString" (.getGeometryType ls)))
      (is (= linestring-gj linestring-str)))))

