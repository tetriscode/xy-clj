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
  (testing "GeoJSON Within Test"
    (is (relation/within? stl missouri))
    (is (relation/within? dfw texas))
    (is (relation/within? mdw illinois))
    (is (relation/within? ord illinois))
    (is (relation/within? {:type "FeatureCollection"
                           :features [mdw ord]}
                          illinois))
    (is (relation/within? {:type "FeatureCollection"
                           :features [mdw ord]}
                          {:type "FeatureCollection"
                           :features [illinois]}))))

(deftest disjoint
  (testing "GeoJSON Disjoint Test"
    (is (relation/disjoint? dfw missouri))
    (is (not (relation/disjoint? stl missouri)))
    (is (not (relation/disjoint? {:type "FeatureCollection"
                                :features [mdw ord]}
                               illinois)))
    (is (relation/disjoint? {:type "FeatureCollection"
                             :features [dfw stl]}
                            {:type "FeatureCollection"
                             :features [illinois]}))))

(deftest contained
  (testing "GeoJSON Contained Test"
    (is (relation/contained? missouri stl))
    (is (relation/contained? texas dfw))
    (is (relation/contained? illinois mdw))
    (is (relation/contained? illinois ord))
    (is (not (relation/contained? illinois missouri)))))

(deftest touches
  (testing "GeoJSON Touches test"
    (is (relation/touches? missouri illinois))
    (is (not (relation/touches? missouri texas)))))

(deftest crosses
  (testing "GeoJSON Crosses test"
    (is (not (relation/crosses? missouri texas)))))

(deftest overlaps
  (testing "GeoJSON Overlaps test"
    (is (not (relation/overlaps? missouri texas)))))

(deftest covers
  (testing "GeoJSON Covers test"
    (is (not (relation/covers? missouri texas)))))

