(ns todo-server.core-test
  (:require [clojure.test :refer [deftest use-fixtures]]
            [midje.sweet :refer :all]
            [todo-server.core :as server]
            [io.pedestal.http :as http]
            [io.pedestal.test :refer [response-for]]
            [clojure.data.json :as json]
            [clojure.java.jdbc :as j]
            [clojure.java.io :as io]))

(def service-fn (-> server/service
                    http/dev-interceptors
                    http/create-server
                    ::http/service-fn))

(defn my-fixtures
  [f]
  (j/execute! (assoc server/db :dbname "")
              "DROP DATABASE IF EXISTS app;" {:transaction? false})
  (j/execute! (assoc server/db :dbname "")
              "CREATE DATABASE app;" {:transaction? false})
  (j/execute! server/db (slurp (io/resource "schema.sql")))
  (f))

(use-fixtures :each my-fixtures)

(defn request
  [method path & {:keys [body]
                  :as   args}]
  (let [body (when body (json/write-str body :key-fn #(str (namespace %) "/" (name %))))
        args (cond-> args
                     body (assoc :body body))]
    (-> (apply response-for service-fn method path (mapcat identity args))
        (update :body #(try
                         (json/read-str % :key-fn keyword)
                         (catch Throwable _ %))))))

(deftest integracao
  (let [my-id (atom nil)]
    (fact
      "Listando todos vazios"
      (request :get "/todo")
      => (contains {:body   empty?
                    :status 200}))
    (fact
      "Criando todo"
      (request :post "/todo"
               :body {:todo/text "12223"})
      => (contains {:body   (contains {:todo/text "12223"
                                       :todo/id   number?})
                    :status 201}))
    (fact
      "Listando todos"
      (-> (request :get "/todo") :body first :todo/id)
      => #(number? (reset! my-id %)))
    (fact
      "GET individual"
      (-> (request :get (str "/todo/" @my-id))
          :body)
      => {:todo/id   1
          :todo/text "12223"})
    (fact
      "PUT"
      (-> (request :put (str "/todo/" @my-id)
                   :body {:todo/text "xxxyyy"})
          :body)
      => (contains {:todo/id   @my-id
                    :todo/text "xxxyyy"}))
    (fact
      "DELETE"
      (-> (request :delete (str "/todo/" @my-id))
          :status)
      => 204)
    (fact
      "Listando todos vazios novamente"
      (request :get "/todo")
      => (contains {:body   empty?
                    :status 200}))))
