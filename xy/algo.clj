(ns xy.algo
  (:require [xy.shapes :as shapes])
  (:import (org.locationtech.jts.algorithm Centroid ConvexHull)
           (org.locationtech.jts.triangulate VoronoiDiagramBuilder)))

(defn centroid
  "Returns the centroid fo the geom"
  [geom]
  (Centroid/getCentroid geom))

(defn convex-hull
  "Retunrs a convex hull of the geom"
  [geom]
  (-> (ConvexHull. geom)
      (.getConvexHull)))

(defn voronoi
  "Returns a geometrycollection of polygons of the
  voronoi diagram"
  [geom]
  (let [builder (VoronoiDiagramBuilder.)]
    (do
      (.setSites builder geom)
      (.getDiagram builder shapes/geometry-factory))))

