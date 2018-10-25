(ns todo-server.core
  (:require [io.pedestal.http :as http]
            [clojure.data.json :as json]
            [clojure.java.jdbc :as j]
            [clojure.string :as string]
            [io.pedestal.http.route :as route]))

(def db
  {:dbtype   "postgresql"
   :dbname   "app"
   :host     "localhost"
   :user     "postgres"
   :password "postgres"})

(defn list-todo
  [_]
  {:body   (for [{:keys [id data]} (j/query db "SELECT * FROM todo")]
             {:todo/text data
              :todo/id   id})
   :status 200})

(defn create-todo
  [{{:keys [todo/text]} :params}]
  (let [{:keys [data id]} (first (j/insert! db :todo {:todo/data text}))]
    {:body   {:todo/text data
              :todo/id   id}
     :status 201}))

(defn get-todo
  [{{:keys [id]} :path-params}]
  (let [{:keys [id data]} (first (j/query db ["SELECT * FROM todo WHERE id = ?" (bigint id)]))]
    {:body   {:todo/text data
              :todo/id   id}
     :status 200}))

(defn update-todo
  [{{:keys [id]}        :path-params
    {:keys [todo/text]} :params
    :as                 req}]
  (j/update! db :todo {:data text} ["id = ?" (bigint id)])
  (get-todo req))

(defn delete-todo
  [{{:keys [id]} :path-params}]
  (j/delete! db :todo ["id = ?" (bigint id)])
  {:status 204})

(def ->json
  {:name  ::json
   :enter (fn [{{:keys [body request-method]} :request
                :as            ctx}]
            (if-not (contains? #{:post} request-method)
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

