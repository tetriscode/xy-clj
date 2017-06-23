(ns xy.shapes
  (:import (org.locationtech.jts.geom Coordinate
                                      Point
                                      MultiPoint
                                      LinearRing
                                      LineString
                                      MultiLineString
                                      Polygon
                                      MultiPolygon
                                      PrecisionModel
                                      GeometryFactory)))

(def ^GeometryFactory geometry-factory
  (GeometryFactory.))

(defn coordinate
  ([x y]
   (Coordinate. x y))
  ([x y z]
   (Coordinate. x y z)))

(defn list->coords [coords]
  (map (fn [[x y]] (coordinate x y)) coords))

(defn point
  ([x y]
   (point [x y]))
  ([x y z]
   (point [x y z]))
  ([coordinates]
   (.createPoint geometry-factory (apply coordinate coordinates))))

(defn multi-point
  [points]
  (.createMultiPoint geometry-factory
                     (into-array Point
                                 (map #(apply point %) points))))

(defn linear-ring
  [coordinates]
  (.createLinearRing geometry-factory
                     (into-array Coordinate
                                 (map #(apply coordinate %) coordinates))))

(defn linestring
  [coordinates]
  (.createLineString geometry-factory
                     (into-array Coordinate
                                 (map #(apply coordinate %) coordinates))))

(defn multi-linestring
  [coordinates]
  (.createMultiLineString geometry-factory
                          (into-array LineString
                                      (map linestring coordinates))))

(defn polygon
  ([coordinates]
   (polygon (first coordinates) (second coordinates)))
  ([outer-ring holes]
   (.createPolygon geometry-factory
                   (linear-ring outer-ring)
                   (into-array LinearRing (map linear-ring holes)))))

(defn multi-polygon
  [polygons]
  (.createMultiPolygon geometry-factory
                       (into-array Polygon (map polygon polygons))))

