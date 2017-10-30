(ns xy.relations)

(defn within?
  "Tests if A is within B"
  [a b]
  (.within a b))

(defn intersects?
  "Tests if A intersects B"
  [a b]
  (.intersects a b))

(defn disjoint?
  "Tests if A is not within B"
  [a b]
  (.disjoint a b))

(defn touches?
  "Tests if A touches B"
  [a b]
  (.touches a b))

(defn crosses?
  "Tests if A crosses B"
  [a b]
  (.crosses a b))

(defn contained?
  "Tests if A contains B"
  [a b]
  (.contains a b))

(defn overlaps?
  "Tests if A overlaps B"
  [a b]
  (.overlaps a b))

(defn covers?
  "Tests if A covers B"
  [a b]
  (.covers a b))

