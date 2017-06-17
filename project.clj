(defproject xy "0.1.0-SNAPSHOT"
  :description "XY: Idiomatic Clojure library for Geo"
  :url "http://github.com/tetriscode/xy"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/data.json "0.2.6"]
                 [org.locationtech.jts/jts-core "1.15.0-SNAPSHOT"
                  :exclusions [xerces/xercesImpl]]]
  :repositories [["jts-snapshots" "https://repo.locationtech.org/content/repositories"]
                 ["clojars" {:sign-releases false}]
                 ["project" "file:repo"]])
