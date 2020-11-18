(ns user
  (:require [todo-server.core :as server]
            [todo-common.core-test]
            [shadow.cljs.devtools.server :as shadow.server]
            [shadow.cljs.devtools.api :as shadow]
            [next.jdbc :as j]
            [clojure.java.io :as io]))

(defn start
  []
  (server/-main)
  (shadow.server/start!)
  (shadow/watch :web))

(defn dev-repl
  []
  #_(f/cljs-repl "dev"))

(defn card-repl
  []
  #_(f/cljs-repl "card"))

(defn stop
  []
  #_(f/stop-figwheel!))

(defn restart
  []
  (server/-main))

(def db
  {:dbtype   "postgresql"
   :dbname   "app"
   :host     "localhost"
   :user     "postgres"
   :password "postgres"})

(defn install-db-schema
  []
  (j/execute! (assoc server/db :dbname "")
              ["DROP DATABASE IF EXISTS app;"]
              {:transaction? false})
  (j/execute! (assoc server/db :dbname "")
              ["CREATE DATABASE app;"]
              {:transaction? false})
  (j/execute! server/db
              [(slurp (io/resource "schema.sql"))]))
