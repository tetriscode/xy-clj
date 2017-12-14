(ns xy.test-utils)

(defn load-geojson [fname]
  (-> (slurp (str "dev-resources/" fname))
      (clojure.string/replace " " "")
      (clojure.string/replace "\n" "")))


