(ns cljs.user
  (:require [todo-common.core-test]
            [todo-client.cards]
            [goog.dom :as gdom]
            [todo-client.core :as client]))

(defn on-jsload
  []
  (let [target (gdom/getElement "app")]
    (client/render target)))
