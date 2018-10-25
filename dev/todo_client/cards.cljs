(ns todo-client.cards
  (:require [todo-client.core :as client]
            [devcards.core :refer-macros [defcard reagent]]
            [reagent.core :as r]))


(defcard my-todo-item
  "*Aqui alguma documentação maneira*
  - com markdown"
  (reagent (fn [state owner]
             (let [st @state]
               [client/view-todo-item (assoc st
                                        :on-click #(js/alert (str "placeholder: " %)))])))
  (r/atom {:todo/id   "123"
           :todo/text "Testando 123"})
  {:inspect-data true
   #_#_:frame true
   #_#_:history true})

(defcard my-input-text
  "*Aqui alguma documentação maneira*
  - com markdown"
  (reagent (fn [state owner]
             (let [st @state]
               [client/view-input (assoc st
                                    :on-change #(swap! state assoc :text %)
                                    :on-click #(swap! state assoc :text (str "_" %)))])))
  (r/atom {:text "123"})
  {:inspect-data true
   #_#_:frame true
   #_#_:history true})
