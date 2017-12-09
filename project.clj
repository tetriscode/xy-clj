(defproject tetriscode/xy "0.8.0"
  :description "XY: A Clojure library for Geo"
  :url "http://github.com/tetriscode/xy"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-RC2"]
                 [org.clojure/spec.alpha "0.1.143"]
                 [org.clojure/data.json "0.2.6"]
                 [org.locationtech.jts/jts-core "1.15.0-uhopper"
                  :exclusions [xerces/xercesImpl]]]
  :plugins [[lein-tar "3.2.0"]
            [lein-bikeshed "0.2.0"]
            [lein-cljfmt "0.5.7"]
            [jonase/eastwood "0.2.4"]
            [lein-kibit "0.1.5"]]
  :repositories [["jts-snapshots" "https://dl.bintray.com/u-hopper/maven/"]
                 ["clojars" {:sign-releases false}]]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]
                   :resource-paths ["dev-resources"]}})
