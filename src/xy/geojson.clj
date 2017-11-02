(ns xy.geojson
  (:require [clojure.spec.alpha :as spec]
            [clojure.data.json :as json]
            [clojure.test.check.generators :as gen]
            [clojure.walk :as walk]
            [xy.shapes :as shapes]))

(defn circle-gen [x y]
  (let [vertices (+ (rand-int 8) 4)
        radius (rand 3)                                     ;2 dec degrees radius length
        rads (/ (* 2.0 Math/PI) vertices)
        pts (map (fn [r]
                   [(+ x (* radius (Math/cos (* r rads))))
                    (+ y (* radius (Math/sin (* rads r))))])
                 (range vertices))]
    (conj pts (last pts))))

(defn line-gen [x y cnt]
  (let [vertices (+ cnt 2)]
    (map (fn []
           [(+ x (rand))
            (+ y (rand))])
         (range vertices))))

;;; geojson
(spec/def :gj/x (spec/double-in :min -175.0 :max 175.0 :NaN? false :infinite? false))
(spec/def :gj/y (spec/double-in :min -85.0 :max 85.0 :NaN? false :infinite? false))
(spec/def :gj/coordinates (spec/with-gen
                            coll?
                            #(gen/fmap (fn [[lon lat]] (list lon lat))
                                       (gen/tuple (spec/gen :gj/x) (spec/gen :gj/y)))))
(spec/def :gjpt/type (spec/with-gen string? #(spec/gen #{"Point"})))
(spec/def :gjlspec/coordinates
  (spec/with-gen
    coll?
    #(gen/fmap (fn
                 [[lon lat cnt]]
                 (line-gen lon lat cnt))
               (gen/tuple (spec/gen :gj/x) (spec/gen :gj/y) (spec/gen pos-int?)))))
(spec/def :gjlspec/type (spec/with-gen string? #(spec/gen #{"LineString"})))
(spec/def :gjpl/coordinates (spec/with-gen
                              coll?
                              #(gen/fmap (fn [[lon lat]] (list (circle-gen lon lat)))
                                         (gen/tuple (spec/gen :gj/x) (spec/gen :gj/y)))))
(spec/def :gjpl/type (spec/with-gen string? #(spec/gen #{"Polygon"})))
(spec/def :gjmpt/coordinates (spec/coll-of :gj/coordinates))
(spec/def :gjmpt/type (spec/with-gen string? #(spec/gen #{"MultiPoint"})))
(spec/def :gjmlspec/coordinates (spec/coll-of :gjlspec/coordinates))
(spec/def :gjmlspec/type (spec/with-gen string? #(spec/gen #{"MultiLineString"})))
(spec/def :gjmpl/coordinates (spec/coll-of :gjpl/coordinates))
(spec/def :gjmpl/type (spec/with-gen string? #(spec/gen #{"MultiPolygon"})))

(def geom-types #{"Point" "Polygon" "LineString"
                  "MultiPolygon" "MultiLineString" "MultiPoint"})
(spec/def :gj/point (spec/keys :req-un [:gjpt/type :gj/coordinates]))
(spec/def :gj/linestring (spec/keys :req-un [:gjlspec/type :gjlspec/coordinates]))
(spec/def :gj/polygon (spec/keys :req-un [:gjpl/type :gjpl/coordinates]))
(spec/def :gj/multipoint (spec/keys :req [:gjmpt/type :gjmpt/coordinates]))
(spec/def :gj/multilinestring (spec/keys :req [:gjmlspec/type :gjmlspec/coordinates]))
(spec/def :gj/multipolygon (spec/keys :req [:gjmpl/type :gjmpl/coordinates]))

(spec/def :gj/type (spec/with-gen
                     (spec/and string? #(contains? geom-types %))
                     #(spec/gen geom-types)))
(spec/def :gj/geometrytypes (spec/or :point :gj/point
                                     :linestring :gj/linestring
                                     :polygon :gj/polygon
                                     :multipoint :gj/multipoint
                                     :multilinestring :gj/multilinestring
                                     :multipolygon :gj/multipolygon))
(spec/def :gjpt/geometry :gj/point)
(spec/def :gjpl/geometry :gj/polygon)
(spec/def :gjlspec/geometry :gj/linestring)
(spec/def :gj/geometry :gj/geometrytypes)
(spec/def :gfeature/id (spec/and string? #(> (count %) 0)))
(spec/def :gfeature/properties (spec/with-gen
                                 (spec/or :nil nil? :map map?)
                                 #(spec/gen #{{}})))
(spec/def :gfeature/type #{"Feature"})
; Single geojson point feature
(spec/def ::pointfeature-spec (spec/keys :req-un
                                         [:gfeature/id :gfeature/type
                                          :gfeature/properties :gjpt/geometry]))
; Single geojson polygon feature
(spec/def ::polygonfeature-spec (spec/keys :req-un
                                           [:gfeature/id :gfeature/type
                                            :gfeature/properties :gjpl/geometry]))
; Single geojson linestring feature
(spec/def ::linestringfeature-spec (spec/keys :req-un
                                              [:gfeature/id :gfeature/type
                                               :gfeature/properties :gjlspec/geometry]))

; Single geojson feature
(spec/def ::feature-spec (spec/keys :req-un
                                    [:gfeature/id :gfeature/type
                                     :gj/geometry :gfeature/properties]))
(spec/def :gj/features (spec/coll-of ::feature-spec))
(spec/def :gjpoly/features (spec/coll-of ::polygonfeature-spec :min-count 1))
(spec/def :fcgj/type (spec/with-gen
                       (spec/and string? #(contains? #{"FeatureCollection"} %))
                       #(spec/gen #{"FeatureCollection"})))
(spec/def ::featurecollection-spec (spec/keys :req-un [:fcgj/type :gj/features]))
(spec/def ::featurecollectionpolygon-spec (spec/keys :req-un
                                                     [:fcgj/type :gjpoly/features]))

(defn list->coords [coords]
  (map (fn [[x y]] (shapes/coordinate x y)) coords))

(defmulti parse "Takes a geojson parsed string->map" #(-> (:type %) keyword))

(defmulti write "Takes a jts and produces a geojson string"
          (fn [geojson-map]
            (case (:type geojson-map)
              :FeatureCollection :FeatureCollection
              :Feature :Feature
              :GeometryCollection :GeometryCollection
              (keyword (.getGeometryType geojson-map)))))

(defmethod parse :FeatureCollection
  [geojson-map]
  (assoc geojson-map :type :FeatureCollection
                     :features (map parse (:features geojson-map))))

(defmethod write :FeatureCollection
  [jts-geom]
  (assoc jts-geom :features (map write (:features jts-geom))))

(defmethod parse :Feature
  [geojson-map]
  (assoc geojson-map :type :Feature :geometry (parse (:geometry geojson-map))))

(defmethod write :Feature
  [jts-geom]
  (assoc jts-geom :geometry (write (:geometry jts-geom))))

(defmethod parse :GeometryCollection
  [geojson-map]
  {:type :GeometryCollection
   :geometries (map parse (:geometries geojson-map))})

(defmethod write :GeometryCollection
  [jts-geom]
  (assoc jts-geom :geometries (map write (:geometries jts-geom))))

(defmethod parse :Point
  [geojson-map]
  (shapes/point (:coordinates geojson-map)))

(defmethod write :Point
  [jts-geom]
  {:type :Point :coordinates [(.getX jts-geom) (.getY jts-geom)]})

(defmethod parse :MultiPoint
  [geojson-map]
  (shapes/multi-point (:coordinates geojson-map)))

(defmethod write :MultiPoint
  [jts-geom]
  {:type        :MultiPoint
   :coordinates (map (fn [idx]
                       (let [geom (.getGeometryN jts-geom idx)]
                         [(.getX geom) (.getY geom)]))
                     (range (.getNumGeometries jts-geom)))})

(defmethod parse :LineString
  [geojson-map]
  (shapes/linestring (:coordinates geojson-map)))

(defmethod write :LineString
  [jts-geom]
  {:type        :LineString
   :coordinates (map (fn [idx]
                       (let [pt (.getPointN jts-geom idx)]
                         [(.getX pt) (.getY pt)]))
                     (range (.getNumPoints jts-geom)))})

(defmethod parse :MultiLineString
  [geojson-map]
  (shapes/multi-linestring (:coordinates geojson-map)))

(defmethod write :MultiLineString
  [jts-geom]
  {:type        :MultiLineString
   :coordinates (map (fn [idx]
                       (let [geom (.getGeometryN jts-geom idx)]
                         (map (fn [lidx]
                                (let [pt (.getPointN geom lidx)]
                                  [(.getX pt) (.getY pt)]))
                              (range (.getNumPoints geom)))))
                     (range (.getNumGeometries jts-geom)))})

(defmethod parse :Polygon
  [geojson-map]
  (shapes/polygon (first (:coordinates geojson-map)) (rest (:coordinates geojson-map))))

(defn jts-polygon->coords [jts-geom]
  (concat
    [(let [shell (.getExteriorRing jts-geom)]
       (map (fn [idx]
              (let [pt (.getPointN shell idx)]
                [(.getX pt) (.getY pt)]))
            (range (.getNumPoints shell))))]
    (map
      (fn [hole-idx]
        (let [hole (.getInteriorRingN jts-geom hole-idx)]
          (map (fn [idx]
                 (let [pt (.getPointN hole idx)]
                   [(.getX pt) (.getY pt)]))
               (range (.getNumPoints hole)))))
      (range (.getNumInteriorRing jts-geom)))))

(defmethod write :Polygon
  [jts-geom]
  {:type        :Polygon
   :coordinates (jts-polygon->coords jts-geom)})

(defmethod parse :MultiPolygon
  [geojson-map]
  (shapes/multi-polygon (:coordinates geojson-map)))

(defmethod write :MultiPolygon
  [jts-geom]
  {:type        :MultiPolygon
   :coordinates (map
                  #(jts-polygon->coords (.getGeometryN jts-geom %))
                  (range (.getNumGeometries jts-geom)))})

(defmethod parse :default [_] {:id 0})

(defn str->map
  "Parses a string containing geojson and returns a geojson map
  with the :geometry a jts geometry"
  [geojson-str]
  (parse (walk/keywordize-keys (clojure.data.json/read-str geojson-str))))

(defn map->str
  "Takes a GeoJSON Map with a JTS Geom in the :geometry key
  and returns a geojson string"
  [jts-geom]
  (json/write-str (write jts-geom)))
