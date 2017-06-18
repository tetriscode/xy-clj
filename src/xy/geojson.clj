(ns xy.geojson
  (:require [clojure.spec :as spec]
            [clojure.data.json :as json]
            [clojure.test.check.generators :as gen]
            [clojure.walk :as walk]
            [xy.shapes :as shapes]))

(defn circle-gen [x y]
  (let [vertices (+ (rand-int 8) 4)
        radius (rand 3) ;2 dec degrees radius length
        rads (/ (* 2.0 Math/PI) vertices)
        pts (map (fn [r]
                   [(+ x (* radius (Math/cos (* r rads))))
                    (+ y (* radius (Math/sin (* rads r))))])
                 (range vertices))]
    (conj pts (last pts))))

(defn line-gen [x y cnt]
  (let [vertices  (+ cnt 2)]
    (map (fn  []
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
                  "MultiPolygon" "MultiLinestring" "MultiPoint"})
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

(defmulti parse "Takes a geojson parsed string->map" :type)

(defmethod parse "FeatureCollection"
  [val]
  (map parse (:features val)))

(defmethod parse "Feature"
  [val]
  (parse (:geometry val)))

(defmethod parse "GeometryCollection"
  [val]
  (map parse (:geometries val)))

(defmethod parse "Geometry"
  [val]
  (parse (:geometry val)))

(defmethod parse "Point"
  [val]
  (shapes/point (:coordinates val)))

(defmethod parse "MultiPoint"
  [val]
  (shapes/multi-point (:coordinates val)))

(defmethod parse "Linestring"
  [val]
  (shapes/linestring (list->coords (:coordinates val))))

(defmethod parse "MultiLinestring"
  [val]
  (shapes/multi-linestring (:coordinates val)))

(defmethod parse "Polygon"
  [val]
  (shapes/polygon (list->coords (first (:coordinates val)))))

(defmethod parse "MultiPolygon"
  [val]
  (shapes/multi-polygon (:coordinates val)))

(defn parse-str [val-str]
  (parse (walk/keywordize-keys (clojure.data.json/read-str val-str))))

(defn stringify [val]
  (clojure.data.json/write-str val))