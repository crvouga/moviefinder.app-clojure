(defproject app "0.1.0-SNAPSHOT"
  :description "A simple Clojure web server with hot reloading"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [org.xerial/sqlite-jdbc "3.23.1"]
                 [hiccup "2.0.0-RC3"]
                 [ring/ring-core "1.9.3"]
                 [ring/ring-jetty-adapter "1.9.3"]
                 [ring/ring-devel "1.9.3"]
                 [cheshire "5.10.0"]
                 [clj-http "3.13.0"]]
  :main ^:skip-aot moviefinder-app.main
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[ring/ring-mock "0.4.0"]]}}
  :repl-options {:init-ns moviefinder-app.main})
