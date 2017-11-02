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
(def feature-str (load-geojson "feature.geojson"))
(def featurecollection-str (load-geojson "featurecollection.geojson"))

(deftest point-test
  (testing "Point string IO"
    (let [pt (gj/str->map point-str)
          pt-gj (gj/map->str pt)]
      (is (some? pt))
      (is (= "Point" (.getGeometryType pt)))
      (is (= pt-gj point-str)))))

(deftest polygon-test
  (testing "Polygon string IO"
    (let [poly (gj/str->map polygon-str)
          poly-gj (gj/map->str poly)]
      (is (some? poly))
      (is (= "Polygon" (.getGeometryType poly)))
      (is (= polygon-str poly-gj)))))

(deftest linestring-test
  (testing "LinePtring string IO"
    (let [ls (gj/str->map linestring-str)
          linestring-gj (gj/map->str ls)]
      (is (some? ls))
      (is (= "LineString" (.getGeometryType ls)))
      (is (= linestring-gj linestring-str)))))

(deftest multipoint-test
  (testing "MultiPoint Test"
    (let [mp (gj/str->map multipoint-str)
          multipoint-gj (gj/map->str mp)]
         (is (some? mp))
         (is (= "MultiPoint" (.getGeometryType mp)))
         (is (= multipoint-gj multipoint-str)))))

(deftest multilinestring-test
  (testing "MultiLineString Test"
    (let [mls (gj/str->map multilinestring-str)
          multilinestring-gj (gj/map->str mls)]
         (is (some? mls))
         (is (= "MultiLineString" (.getGeometryType mls)))
         (is (= multilinestring-gj multilinestring-str)))))

(deftest multipolygon-test
         (testing "MultiPolygon Test"
                  (let [mp (gj/str->map multipolygon-str)
                        multipolygon-gj (gj/map->str mp)]
                       (is (some? mp))
                       (is (= "MultiPolygon" (.getGeometryType mp)))
                       (is (= multipolygon-gj multipolygon-str)))))

(deftest feature-test
  (testing "Feature Test"
    (let [f (gj/str->map feature-str)
          feature-gj (gj/map->str f)]
      (is (and (some? f) (some? feature-gj)))
      (is (= "Feature" (name (f :type))))
      (is (= feature-gj feature-str)))))

(deftest featurecollection-test
  (testing "FeatureCollection test"
    (let [fc (gj/str->map featurecollection-str)
          fc-gj (gj/map->str fc)]
      (is (and (some? fc) (some? fc-gj)))
      (is (= "FeatureCollection" (name (:type fc))))
      (is (= featurecollection-str fc-gj)))))