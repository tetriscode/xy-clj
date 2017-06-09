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

(defn point
  ([x y]
   (point (coordinate x y)))
  ([x y z]
   (point (coordinate x y z)))
  ([^Coordinate coordinate]
   (.createPoint geometry-factory coordinate)))

(defn multi-point
  [points]
  (.createMultiPoint geometry-factory (into-array Point points)))

(defn linear-ring
  [coordinates]
  (.createLinearRing geometry-factory (into-array Coordinate coordinates)))

(defn linestring
  [coordinates]
  (.createLineString geometry-factory (into-array Coordinate coordinates)))

(defn multi-linestring
  [linestrings]
  (.createMultiLineString geometry-factory (into-array LineString linestrings)))

(defn polygon
  ([outer-ring]
   (polygon outer-ring nil))
  ([outer-ring holes]
   (.createPolygon geometry-factory outer-ring (into-array LinearRing holes))))

(defn multi-polygon
  [polygons]
  (.createMultiPolygon geometry-factory (into-array Polygon polygons)))

