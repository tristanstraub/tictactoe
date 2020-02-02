(ns tictactoe.style
  (:require [garden-mower.core]
            [garden.core]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(defonce tailwind-css
  (delay (garden-mower.core/parse
          (slurp "https://cdn.tailwindcdn.com/1.1.4/tailwind.min.css"))))

(defn attributes
  [& selectors]
  (apply garden-mower.core/attributes @tailwind-css selectors))

(defmacro css
  []
  (garden.core/css
   {:pretty-print? false}
   [[:body {:font-size "24px"}]
    [:.container (attributes :.mx-auto)]]))
