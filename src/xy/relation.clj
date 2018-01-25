(ns xy.relation)

(defn loop-a
  "This function loops over the variable a if it is a collection
  Geometry such as FeatureCollection, Geometry Collection"
  [func sel-key a b]
  (loop [coll (sel-key a)]
    (if-let [first-a (first coll)]
      (if (func first-a b)
        (recur (rest coll))
        false)
      true)))

(defn loop-b
  [func a b]
  (case (:type b)
    "GeometryCollection" (loop [b-geoms (:geometries b)]
                           (if b-geoms
                             (if (loop-b func a (first b-geoms))
                               (recur (rest b-geoms))
                               false)
                             true))
    "FeatureCollection" (loop [b-feats (:features b)]
                          (if-let [first-b (first b-feats)]
                            (if (loop-b func a first-b)
                              (recur (rest b-feats))
                              false)
                            true))
    "Feature" (loop-b func a (:geometry b))
    (func a b))) ; default case means a and b are both geometries

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;WITHIN;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; This function must be created because .within cannot be passed as a first
;; class function in Clojure
(defn- within-func [a b] (.within a b))

(defn- within
  "Tests if A is within B"
  [a b]
  (loop-b within-func a b))

(defmulti within? "within? for GeoJSON Maps" :type)

(defmethod within? "GeometryCollection"
  [a b]
  (loop-a within? :geometries a b))

(defmethod within? "FeatureCollection"
  [a b]
  (loop-a within? :features a b))

(defmethod within? "Feature"
  [a b]
  (within? (:geometry a) b))

(defmethod within? :default
  [a b]
  (within a b))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;INTERSECTS;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn intersects
  "Tests if A intersects B"
  [a b]
  (.intersects a b))

(defmulti intersects? "intersects for GeoJSON Maps" :type)

(defmethod intersects? "GeometryCollection"
  [a b]
  (loop-a intersects? :geometries a b))

(defmethod intersects? "FeatureCollection"
  [a b]
  (loop-a intersects? :features a b))

(defmethod intersects? "Feature"
  [a b]
  (intersects? (:geometry a) b))

(defmethod intersects? :default
  [a b]
  (intersects a b))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;DISJOINT;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- disjoint-func [a b] (.disjoint a b))

(defn- disjoint
  "Tests if A is not within B"
  [a b]
  (loop-b disjoint-func a b))

(defmulti disjoint? "disjoint for GeoJSON Maps" :type)

(defmethod disjoint? "GeometryCollection"
  [a b]
  (loop-a disjoint? :geometries a b))

(defmethod disjoint? "FeatureCollection"
  [a b]
  (loop-a disjoint? :features a b))

(defmethod disjoint? "Feature"
  [a b]
  (disjoint? (:geometry a) b))

(defmethod disjoint? :default
  [a b]
  (disjoint a b))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;TOUCHES;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- touches-func [a b] (.touches a b))

(defn touches
  "Tests if A touches B"
  [a b]
  (loop-b touches-func a b))

(defmulti touches? "touches for GeoJSON Maps" :type)

(defmethod touches? "GeometryCollection"
  [a b]
  (loop-a touches? :geometries a b))

(defmethod touches? "FeatureCollection"
  [a b]
  (loop-a touches? :features a b))

(defmethod touches? "Feature"
  [a b]
  (touches? (:geometry a) b))

(defmethod touches? :default
  [a b]
  (touches a b))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;CROSSES;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn crosses-func [a b] (.crosses a b))

(defn crosses
  "Tests if A crosses B"
  [a b]
  (loop-b crosses-func a b))

(defmulti crosses? "crosses for GeoJSON Maps" :type)

(defmethod crosses? "GeometryCollection"
  [a b]
  (loop-a crosses? :geometries a b))

(defmethod crosses? "FeatureCollection"
  [a b]
  (loop-a crosses? :features a b))

(defmethod crosses? "Feature"
  [a b]
  (crosses? (:geometry a) b))

(defmethod crosses? :default
  [a b]
  (crosses a b))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;CONTAINED;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn contained-func [a b] (.contains a b))

(defn contained
  "Tests if A contains B"
  [a b]
  (loop-b contained-func a b))

(defmulti contained? "contained for GeoJSON Maps" :type)

(defmethod contained? "GeometryCollection"
  [a b]
  (loop-a contained? :geometries a b))

(defmethod contained? "FeatureCollection"
  [a b]
  (loop-a contained? :features a b))

(defmethod contained? "Feature"
  [a b]
  (contained? (:geometry a) b))

(defmethod contained? :default
  [a b]
  (contained a b))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;OVERLAPS;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn overlaps-func [a b] (.overlaps a b))

(defn overlaps
  "Tests if A overlaps B"
  [a b]
  (loop-b overlaps-func a b))

(defmulti overlaps? "overlaps for GeoJSON Maps" :type)

(defmethod overlaps? "GeometryCollection"
  [a b]
  (loop-a overlaps? :geometries a b))

(defmethod overlaps? "FeatureCollection"
  [a b]
  (loop-a overlaps? :features a b))

(defmethod overlaps? "Feature"
  [a b]
  (overlaps? (:geometry a) b))

(defmethod overlaps? :default
  [a b]
  (overlaps a b))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;COVERS;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn covers-func [a b] (.covers a b))

(defn covers
  "Tests if A covers B"
  [a b]
  (loop-b covers-func a b))

(defmulti covers? "covers for GeoJSON Maps" :type)

(defmethod covers? "GeometryCollection"
  [a b]
  (loop-a covers? :geometries a b))

(defmethod covers? "FeatureCollection"
  [a b]
  (loop-a covers? :features a b))

(defmethod covers? "Feature"
  [a b]
  (covers? (:geometry a) b))

(defmethod covers? :default
  [a b]
  (covers a b))

