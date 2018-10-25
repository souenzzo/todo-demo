(defproject todo-demo "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.0-beta4"]
                 ;; jvm11
                 [org.clojure/core.rrb-vector "0.0.13"]]
  :aliases {"fullstack"       ["with-profile" "+dev,+client,+server" "repl"]
            "cljs-prod-build" ["with-profile" "client" "do" "clean" ["run" "-m" "cljs.main" "-co" "resources/build.edn" "-v" "-c"]]
            "final-jar"       ["do" "cljs-prod-build" ["with-profile" "server" "uberjar"]]}
  :profiles {:server  {:dependencies   [[org.clojure/clojure "1.10.0-beta4"]
                                        [io.pedestal/pedestal.jetty "0.5.4"]
                                        [io.pedestal/pedestal.service "0.5.4"]
                                        [org.clojure/java.jdbc "0.7.8"]
                                        [org.postgresql/postgresql "42.2.5"]
                                        ;; ignore logging
                                        [org.slf4j/slf4j-nop "1.8.0-beta2"]]
                       :source-paths   ["src"]
                       :resource-paths ["resources"]
                       :main           todo-server.core}
             :uberjar {:aot :all}
             :client  {:source-paths  ["src"]
                       :clean-targets ^{:protect false} ["resources/public/js" "out"]
                       :dependencies  [[org.clojure/clojurescript "1.10.339"]
                                       [reagent/reagent "0.8.1" :exclusions [cljsjs/react
                                                                             cljsjs/react-dom
                                                                             cljsjs/react-dom-server
                                                                             cljsjs/create-react-class]]]}
             :dev     {:source-paths ["src" "dev" "test"]
                       :dependencies [[figwheel-sidecar/figwheel-sidecar "0.5.17"]
                                      [cider/piggieback "0.3.10"]
                                      [midje/midje "1.9.4"]
                                      [devcards/devcards "0.2.6" :exclusions [cljsjs/react
                                                                              cljsjs/create-react-class
                                                                              cljsjs/react-dom
                                                                              cljsjs/marked]]
                                      [org.clojure/test.check "0.10.0-alpha3"]]
                       :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}
                       :main         user}})
