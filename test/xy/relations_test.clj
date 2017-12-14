(ns xy.relations-test
  (:require [clojure.test :refer :all]
            [xy.geojson :as geojson]
            [xy.relation :as relation]
            [xy.test-utils :refer [load-geojson]]))

(def states (geojson/str->map (load-geojson "states.geojson")))
(def airports (geojson/str->map (load-geojson "airports.json")))

(defn get-state [state]
  (first (filter #(= (get-in % [:properties :NAME]) state) (:features states))))

(defn get-airport [code]
  (first (filter #(= (get-in % [:properties :code]) code) (:features airports))))

(def texas (get-state "Texas"))
(def missouri (get-state "Missouri"))
(def illinois (get-state "Illinois"))

(def dfw (get-airport "DFW"))
(def stl (get-airport "STL"))
(def mdw (get-airport "MDW"))
(def ord (get-airport "ORD"))

(deftest within
  (testing "Feature Collection in Polygon Test"
    (is (relation/within? stl missouri))
    (is (relation/within? dfw texas))
    (is (relation/within? mdw illinois))
    (is (relation/within? ord illinois))
    (is (relation/within? {:type "FeatureCollection"
                           :features [mdw ord]}
                          illinois))))
