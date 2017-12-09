(ns xy.relation)

(defn loop-a [func sel-key a b]
  (loop [coll (sel-key a)]
    (if coll
      (if (func (first coll) b)
        (recur (rest coll))
        false)
      true)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;WITHIN;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- within
  "Tests if A is within B"
  [a b]
  (case (:type b)
    "GeometryCollection" (loop [gs (:geometries b)]
                           (if gs
                             (if (.within a (first gs))
                               (recur (rest gs))
                               false)
                             true))
    "FeatureCollection" (loop [gs (:features b)]
                          (if gs
                            (if (.within a (first gs))
                              (recur (rest gs))
                              false)
                            true))
    "Feature" (.within a (:geometry b))
    (.within a b)))

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

(defmethod intersects? :GeometryCollection
  [a b]
  (loop-a intersects? :geometries a b))

(defmethod intersects? :FeatureCollection
  [a b]
  (loop-a intersects? :features a b))

(defmethod intersects? :Feature
  [a b]
  (intersects? (:geometry a) b))

(defmethod intersects? :default
  [a b]
  (intersects a b))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;DISJOINT;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn- disjoint
  "Tests if A is not within B"
  [a b]
  (.disjoint a b))

(defmulti disjoint? "disjoint for GeoJSON Maps" :type)

(defmethod disjoint? :GeometryCollection
  [a b]
  (loop-a disjoint? :geometries a b))

(defmethod disjoint? :FeatureCollection
  [a b]
  (loop-a disjoint? :features a b))

(defmethod disjoint? :Feature
  [a b]
  (disjoint? (:geometry a) b))

(defmethod disjoint? :default
  [a b]
  (disjoint a b))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;TOUCHES;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn touches
  "Tests if A touches B"
  [a b]
  (.touches a b))

(defmulti touches? "touches for GeoJSON Maps" :type)

(defmethod touches? :GeometryCollection
  [a b]
  (loop-a touches? :geometries a b))

(defmethod touches? :FeatureCollection
  [a b]
  (loop-a touches? :features a b))

(defmethod touches? :Feature
  [a b]
  (touches? (:geometry a) b))

(defmethod touches? :default
  [a b]
  (touches a b))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;CROSSES;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn crosses
  "Tests if A crosses B"
  [a b]
  (.crosses a b))

(defmulti crosses? "crosses for GeoJSON Maps" :type)

(defmethod crosses? :GeometryCollection
  [a b]
  (loop-a crosses? :geometries a b))

(defmethod crosses? :FeatureCollection
  [a b]
  (loop-a crosses? :features a b))

(defmethod crosses? :Feature
  [a b]
  (crosses? (:geometry a) b))

(defmethod crosses? :default
  [a b]
  (crosses a b))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;CONTAINED;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn contained
  "Tests if A contains B"
  [a b]
  (.contains a b))

(defmulti contained? "contained for GeoJSON Maps" :type)

(defmethod contained? :GeometryCollection
  [a b]
  (loop-a contained? :geometries a b))

(defmethod contained? :FeatureCollection
  [a b]
  (loop-a contained? :features a b))

(defmethod contained? :Feature
  [a b]
  (contained? (:geometry a) b))

(defmethod contained? :default
  [a b]
  (contained a b))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;OVERLAPS;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn overlaps
  "Tests if A overlaps B"
  [a b]
  (.overlaps a b))

(defmulti overlaps? "overlaps for GeoJSON Maps" :type)

(defmethod overlaps? :GeometryCollection
  [a b]
  (loop-a overlaps? :geometries a b))

(defmethod overlaps? :FeatureCollection
  [a b]
  (loop-a overlaps? :features a b))

(defmethod overlaps? :Feature
  [a b]
  (overlaps? (:geometry a) b))

(defmethod overlaps? :default
  [a b]
  (overlaps a b))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;COVERS;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn covers
  "Tests if A covers B"
  [a b]
  (.covers a b))

(defmulti covers? "covers for GeoJSON Maps" :type)

(defmethod covers? :GeometryCollection
  [a b]
  (loop-a covers? :geometries a b))

(defmethod covers? :FeatureCollection
  [a b]
  (loop-a covers? :features a b))

(defmethod covers? :Feature
  [a b]
  (covers? (:geometry a) b))

(defmethod covers? :default
  [a b]
  (covers a b))

