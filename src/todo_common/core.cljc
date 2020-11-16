(ns todo-common.core
  (:require [clojure.string :as string]))

(defn index-by
  ([f] (map (juxt f identity)))
  ([f coll]
   (into {}
         (index-by f)
         coll)))

(defn valid-todo?
  [{:keys [todo/text]}]
  (not (string/blank? text)))
