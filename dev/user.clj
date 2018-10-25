(ns user
  (:require [todo-server.core :as server]
            [figwheel-sidecar.repl-api :as f]
            [clojure.java.shell :as sh]
            [clojure.java.io :as io]
            [todo-common.core-test]
            [clojure.edn :as edn]))

(def compiler
  (-> "build.edn"
      io/resource
      slurp
      edn/read-string
      (update :closure-defines merge '{goog.asserts.ENABLE_ASSERTS true
                                       goog.DEBUG                  true})
      (dissoc :optimizations)
      (assoc :elide-asserts false)))

(def dev-build
  {:id           "dev"
   :source-paths ["src" "dev"]
   :figwheel     '{:on-jsload cljs.user/on-jsload}
   :compiler     (assoc compiler
                   :main 'cljs.user
                   :asset-path "/js/out"
                   :output-dir "resources/public/js/out"
                   :output-to "resources/public/js/app.js")})

(def card-build
  {:id           "card"
   :source-paths ["src" "dev" "test"]
   :figwheel     {:devcards true}
   :compiler     (assoc compiler
                   :main 'cljs.user
                   :source-map-timestamp true
                   :asset-path "/js/cards"
                   :output-dir "resources/public/js/cards"
                   :output-to "resources/public/js/cards.js")})

(defn start
  []
  (time
    (do (sh/sh "yarn" "install")
        (sh/sh "yarn" "webpack")
        (f/start-figwheel! {:builds          [card-build
                                              dev-build]
                            :builds-to-start ["dev" "card"]}))))

(defn dev-repl
  []
  (f/cljs-repl "dev"))

(defn card-repl
  []
  (f/cljs-repl "card"))

(defn stop
  []
  (f/stop-figwheel!))

(defn restart
  []
  (server/-main))

(def db
  {:dbtype   "postgresql"
   :dbname   "app"
   :host     "localhost"
   :user     "postgres"
   :password "postgres"})
