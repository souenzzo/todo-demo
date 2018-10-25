(ns todo-client.core
  (:refer-clojure :exclude [List])
  (:require [material-ui.core :as m]
            [todo-common.core :refer [index-by]]
            [reagent.core :as r]))

(def Button (r/adapt-react-class m/Button))
(def Select (r/adapt-react-class m/Select))
(def List (r/adapt-react-class m/List))
(def ListItem (r/adapt-react-class m/ListItem))
(def Divider (r/adapt-react-class m/Divider))
(def MenuItem (r/adapt-react-class m/MenuItem))
(def Dialog (r/adapt-react-class m/Dialog))
(def DialogTitle (r/adapt-react-class m/DialogTitle))

(defn fetch
  ([opts then] (fetch opts then #(.error js/console #js{:fetch %})))
  ([opts then catch]
   (let [url (if (string? opts) opts (:url opts))
         {:keys [method]
          :as   opts} (if (string? opts)
                        {}
                        (let [{:keys [method]} opts]
                          (cond-> opts
                                  true (dissoc :url)
                                  true (update :body clj->js :keyword-fn #(.-fqn %))
                                  true (update :body js/JSON.stringify)
                                  (nil? method) (assoc opts :method "GET")
                                  (contains? #{"GET" "HEAD"} method) (dissoc :body))))
         url (str "http://localhost:8080" url)]
     (doto (js/fetch url (clj->js opts))
       (.then (fn [r]
                (if (contains? #{"HEAD" "DELETE"} method)
                  (then)
                  (.then (.json r) #(then (js->clj % :keywordize-keys true))))))
       (.catch catch)))))

(defn get-todos!
  [atom]
  (fetch {:method "GET"
          :url    "/todo"}
         #(swap! atom assoc :todos (index-by :todo/id %))))

(defn delete-todo!
  [atom id]
  (fetch {:method "DELETE"
          :url    (str "/todo/" id)}
         #(swap! atom update :todos dissoc id)))

(defn add-todo
  [atom todo]
  (fetch {:method "POST"
          :body   todo
          :url    "/todo"}
         (fn [e]
           (swap! atom #(-> %
                            (assoc-in [:todos (:todo/id e)] e)
                            (assoc-in [:todo :todo/text] ""))))))

(defn update-todo
  [atom {:keys [todo/id] :as todo}]
  (fetch {:method "PUT"
          :body   todo
          :url    (str "/todo/" id)}
         #(swap! atom assoc-in [:todos (:todo/id %)] %)))

(defonce state (r/atom {}))

(defn view-todo-item
  [{:keys [todo/text todo/id on-click]}]
  [ListItem {:key      id
             :button   true
             :on-click #(on-click id)}
   text])

(defn view-input
  [{:keys [on-click on-change text]}]
  [:<>
   [:input {:value     text
            :on-change #(on-change (-> % .-target .-value))}]
   [Button {:on-click #(on-click text)} "+"]])

(defn view
  []
  (let [{:keys [todo todos selected]} @state
        {:keys [todo/text]
         :or   {text ""}} todo]
    [:div
     [view-input {:text      text
                  :on-click  #(add-todo state {:todo/text %})
                  :on-change #(swap! state assoc-in [:todo :todo/text] %)}]
     [Dialog
      {:on-close (when (contains? todos selected)
                   ;; ^bug
                   #(swap! state dissoc :selected))
       :open     selected}
      (let [{:keys [todo/text todo/id]} (get todos selected)]
        [:<>
         [DialogTitle
          text
          " "
          [:code id]]
         [Button {} "deletar"]
         [Button {} "editar"]])]
     [Divider]
     [List
      (for [{:keys [todo/id] :as todo} (vals todos)]
        [view-todo-item (assoc todo
                          :on-click #(swap! state assoc :selected %)
                          :key id)])]]))


(defn render
  [target]
  (r/render [view] target))

(defn ^:export main
  [target]
  (get-todos! state)
  (render target))
