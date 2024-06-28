(defproject moviefinder.app "0.1.0-SNAPSHOT"
  :description "A simple Clojure web server with hot reloading"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [org.postgresql/postgresql "42.2.24"]
                 [com.github.seancorfield/honeysql "2.6.1126"]
                 [hiccup "2.0.0-RC3"]
                 [ring/ring-core "1.9.3"]
                 [ring/ring-jetty-adapter "1.9.3"]
                 [ring/ring-devel "1.9.3"]
                 [cheshire "5.10.0"]
                 [clj-http "3.13.0"]
                 [garden "1.3.10"]
                 [eftest "0.6.0"]]
  :plugins [[lein-shell "0.5.0"] 
            [lein-eftest "0.6.0"]]
  :main ^:skip-aot moviefinder-app.main
  :target-path "target/%s"
  :test-paths ["src"]
  :profiles {:dev {:dependencies [[ring/ring-mock "0.4.0"]]}}
  :repl-options {:init-ns moviefinder-app.main}
  :aliases {"test-int" ["shell" "sh" "-c" "INTEGRATION_TEST=true lein eftest"]
            "build" ["shell" "lein" "db-up"]
            "db-up" ["shell" "dbmate" "up"]
            "db-down" ["shell" "dbmate" "down"]
            "db-start" ["shell""docker-compose" "-f" "docker-compose.local.yml" "up" "-d"]
            "db-stop" ["shell" "docker-compose" "-f" "docker-compose.local.yml" "down"]})

