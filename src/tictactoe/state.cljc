(ns tictactoe.state)

(defn empty-board
  []
  (vec (repeat 3 (vec (repeat 3 nil)))))

(defn board-symbol
  [{:keys [board]} [rank file]]
  (get-in board [rank file :symbol]))

(defn next-symbol
  [turn-symbol]
  (case turn-symbol
    :x :o
    :o :x))

(defn end-turn
  [{:keys [board turn-symbol] :as state} [rank file :as move]]
  (if move
    (-> state
        (assoc-in [:board rank file :symbol] turn-symbol)
        (update :turn-symbol next-symbol))
    state))

(defn board-rows
  [board]
  board)

(defn board-columns
  [board]
  (apply mapv vector board))

(defn board-diagonals
  [board]
  [(vec (take 3 (map first (partition 4 (cycle (apply concat board))))))
   (vec (take 3 (map first (partition 2 (cycle (drop 2 (apply concat board)))))))])

(defn board-winner
  [board]
  (first (for [line            (apply concat [(board-rows board)
                                              (board-columns board)
                                              (board-diagonals board)])
               [symbol number] (frequencies (map :symbol line))
               :when (and symbol (= number 3))]
           symbol)))

(defn winner
  [{:keys [board]}]
  (board-winner board))

(defn board-full?
  [board]
  (= 9 (count (remove nil? (apply concat board)))))

(defn draw?
  [{:keys [board] :as state}]
  (and (not (winner state))
       (board-full? board)))

(defn lost?
  [state turn-symbol]
  (let [w (winner state)]
    (and w (not= w turn-symbol))))

(defn win?
  [state turn-symbol]
  (= turn-symbol (winner state)))

(defn win-or-draw?
  [state turn-symbol]
  (or (win? state turn-symbol)
      (draw? state)))

(defn allow-turn?
  [{:keys [board]} [rank file]]
  (and (not (board-winner board))
       (nil? (get-in board [rank file :symbol]))))

(defn allowed-moves
  [state]
  (for [rank (range 3)
        file (range 3)
        :when (allow-turn? state [rank file])]
    [rank file]))

(defn new-state
  []
  {:turn-symbol :x
   :board       (empty-board)})
