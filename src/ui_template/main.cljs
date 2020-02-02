(ns ui-template.main
  (:require-macros [ui-template.style :as style])
  (:require [rum.core :as rum]
            [goog.dom :as dom]
            [cljs.pprint :refer [cl-format]]
            [ui-template.state :as state]
            [ui-template.ai :as ai]))

(defonce state
  (atom (state/new-state)))

(rum/defc board-square
  < rum/reactive
  [state [rank file]]
  [:div
   {:on-click (fn [e]
                (.preventDefault e)
                (when (state/allow-turn? @state [rank file])
                  (swap! state state/end-turn [rank file])))}
   (case (state/board-symbol (rum/react state) [rank file])
     :x "x"
     :o "o"
     "-")])

(rum/defc board
  < rum/reactive
  [state]
  [:div.flex.flex-row.select-none
   (for [rank (range 3)]
     [:div.flex.flex-col {:key rank}
      (for [file (range 3)]
        [:div.w-10.border.border-1.text-center.cursor-pointer
         {:key file}
         (board-square state [rank file])])])])

(rum/defc restart-button
  [state]
  [:button.border {:on-click (fn [e]
                               (.preventDefault e)
                               (reset! state (state/new-state)))}
   "Restart"])

(rum/defc ai-play-turn-button
  [state]
  [:button.border {:on-click (fn [e]
                               (.preventDefault e)
                               (swap! state ai/play-turn))}
   "AI"])

(rum/defc game-message
  < rum/reactive
  [state]
  (let [winner (state/winner (rum/react state))]
    (cond
      winner
      [:div (cl-format nil "~a has won!" (name winner))]

      (state/draw? (rum/react state))
      [:div "It's a draw!"]

      :else
      [:div (cl-format nil "It is ~a's turn" (name (rum/react (rum/cursor-in state [:turn-symbol]))))])))

(rum/defc main
  < rum/reactive
  [state]
  [:span
   [:style (style/css)]
   [:div.container.mt-10
    (restart-button state)
    (ai-play-turn-button state)
    (game-message state)
    (board state)]])

(rum/mount (main state) (dom/getElement "app"))
