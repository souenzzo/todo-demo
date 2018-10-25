(ns todo-common.core
  (:require [clojure.string :as string]))

(defn index-by
  [f coll]
  (-> #(assoc! %1 (f %2) %2)
      (reduce (transient {}) coll)
      persistent!))

(defn valid-todo?
  [{:keys [todo/text]}]
  (not (string/blank? text)))
