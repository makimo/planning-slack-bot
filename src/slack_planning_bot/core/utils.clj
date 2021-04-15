(ns slack-planning-bot.core.utils
  (:require [clj-time.core :as t]
            [clj-time.predicates :as pr]
            [clj-time.format :as f]))

(defn formatDate [date]
  (f/unparse (f/formatter :date) date))

(defn count-work-days [start-date end-date]
  (let [current-day (atom start-date)
        counter     (atom 0)]
    (while (not= (formatDate @current-day) (formatDate end-date))
      (reset! current-day (t/plus @current-day (t/days 1)))
      (if-not (pr/weekend? @current-day) (swap! counter inc)))
    @counter))