(ns todo-common.core-test
  (:require #?@(:clj  [[clojure.test :refer [deftest is testing]]]
                :cljs [[clojure.test :refer [is testing]]
                       [devcards.core :refer-macros [deftest]]])
            [todo-common.core :as common]))

(deftest my-first-test
  "My first test"
  (testing
    (is (= (+ 1 2) 3))
    (is (not= (+ 1 3) 3))))

(deftest common-test
  "Todo is valid?"
  (testing
    (is (true? (common/valid-todo? {:todo/text "aaaa"})))
    (is (false? (common/valid-todo? {:todo/text ""})))))

