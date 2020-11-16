(ns todo-server.core
  (:require [io.pedestal.http :as http]
            [clojure.data.json :as json]
            [next.jdbc :as j]
            [clojure.string :as string]
            [io.pedestal.http.route :as route]
            [clojure.edn :as edn]))

(def db
  {:dbtype   "postgresql"
   :dbname   "app"
   :host     "localhost"
   :user     "postgres"
   :password "postgres"})

(defn list-todo
  [_]
  {:body   (for [{:todo/keys [id data]} (j/execute! db
                                                    ["SELECT * FROM todo"])]
             {:todo/text data
              :todo/id   id})
   :status 200})

(defn create-todo
  [{{:keys [todo/text]} :params}]
  (let [{:todo/keys [data id]} (first (j/execute! db
                                                  ["INSERT INTO todo(data) VALUES (?) RETURNING *"
                                                   text]))]
    {:body   {:todo/text data
              :todo/id   id}
     :status 201}))

(defn get-todo
  [{{:keys [id]} :path-params}]
  (let [{:todo/keys [id data]} (first (j/execute! db
                                                  ["SELECT * FROM todo WHERE id = ?"
                                                   (edn/read-string id)]))]
    (when id
      {:body   {:todo/text data
                :todo/id   id}
       :status 200})))

(defn update-todo
  [{{:keys [id]}        :path-params
    {:keys [todo/text]} :params
    :as                 req}]
  (j/execute! db
              ["UPDATE todo SET data = ? WHERE id = ?"
               text (edn/read-string id)])
  (get-todo req))

(defn delete-todo
  [{{:keys [id]} :path-params}]
  (j/execute! db
              ["DELETE FROM todo WHERE id = ?"
               (edn/read-string id)])
  {:status 204})

(def ->json
  {:name  ::json
   :enter (fn [{{:keys [body request-method]} :request
                :as                           ctx}]
            (if-not (contains? #{:post :put} request-method)
              ctx
              (let [s (slurp body)
                    data (when-not (string/blank? s)
                           (json/read-str s :key-fn keyword))]
                (update-in ctx [:request :params] merge data))))
   :leave (fn [ctx]
            (update-in ctx [:response :body] json/write-str :key-fn #(str (namespace %) "/" (name %))))})

(def routes
  `#{["/todo" :get [->json list-todo]]
     ["/todo" :post [->json create-todo]]
     ["/todo/:id" :get [->json get-todo]]
     ["/todo/:id" :put [->json update-todo]]
     ["/todo/:id" :delete [->json delete-todo]]})

(def service
  (-> {::http/routes            #(route/expand-routes (deref #'routes))
       ::http/port              8080
       ::http/join?             false
       ::http/type              :jetty
       ::http/allowed-origins   {:creds           true
                                 :allowed-origins (constantly true)}
       ::http/container-options {:h2c? true}
       ::http/secure-headers    {:content-security-policy-settings ""}
       ::http/host              "0.0.0.0"}
      http/default-interceptors
      http/dev-interceptors))

(defonce state (atom nil))

(defn -main
  []
  (when @state
    (swap! state (fn [st] (http/stop st) nil)))
  (swap! state (fn [st] (http/start (http/create-server service)))))

