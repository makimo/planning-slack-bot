(ns slack-planning-bot.core.use-case
  (:require [slack-planning-bot.core.entity :as entity]))

(defn send-reminders
  [{:keys [time-tracker name-provider messenger planning-config]}]
  (fn []))

(defn configure-planning
  [{:keys [scheduler planning-config]}]
  (fn [{:keys [next-planning interval day-of-week]}]))