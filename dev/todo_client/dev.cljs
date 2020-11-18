(ns todo-client.dev
  (:require [goog.dom :as gdom]
            [todo-client.core :as client]))

(defn on-jsload
  []
  (let [target (gdom/getElement "app")]
    (client/render target)))
