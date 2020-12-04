(defproject clojure-rest-mongodb "0.1.0-SNAPSHOT"
  :description "Clojure App to serve a MongoDB database through RESTful APIsn"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [ring "1.8.2"]
                 [ring/ring-json "0.5.0"]
                 [compojure "1.6.2"]
                 [org.clojure/data.json "1.0.0"]
                 [com.novemberain/monger "3.1.0"]]
  :main ^:skip-aot clojure-rest-mongodb.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :dev
             {:main clojure-rest-mongodb.core/-dev-main}})
