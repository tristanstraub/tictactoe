(ns ui-template.ai
  (:require [ui-template.state :as state]))

(defn count-winning-ends
  [state remaining-depth]
  (if-let [winner (state/winner state)]
    {winner 1}
    (when (pos? remaining-depth)
      (->> (state/allowed-moves state)
           (map #(state/end-turn state %))
           (map #(count-winning-ends % (dec remaining-depth)))
           (apply merge-with +)))))

(defn non-losing-move?
  [{:keys [turn-symbol] :as state} move]
  (let [state (state/end-turn state move)]
    (or (state/win-or-draw? state turn-symbol)
        (->> (state/allowed-moves state)
             (map #(state/end-turn state %))
             (every? #(not (state/lost? % turn-symbol)))))))

(defn best-move
  ([state]
   (best-move state 4))
  ([{:keys [turn-symbol] :as state} depth]
   (let [moves (state/allowed-moves state)]
     (or (->> moves
              (filter #(state/win? (state/end-turn state %) turn-symbol))
              first)
         (->> moves
              (filter (partial non-losing-move? state))
              (sort-by (comp turn-symbol #(count-winning-ends (state/end-turn state %) depth)))
              last)
         (some-> (seq moves) rand-nth)))))

(defn play-turn
  [{:keys [turn-symbol board] :as state}]
  (let [move (best-move state 2)]
    (state/end-turn state move)))
