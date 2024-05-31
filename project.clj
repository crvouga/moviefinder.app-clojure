(defproject app "0.1.0-SNAPSHOT"
  :description "A simple Clojure web server with hot reloading"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring/ring-core "1.9.3"]
                 [ring/ring-jetty-adapter "1.9.3"]
                 [ring/ring-devel "1.9.3"]] ; Added ring-devel for hot reloading
  :main ^:skip-aot app.core
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[ring/ring-mock "0.4.0"]]}}
  :repl-options {:init-ns app.core})
