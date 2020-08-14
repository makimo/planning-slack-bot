(ns slack-planning-bot.core.use-case
  (:require [slack-planning-bot.core.entity :as entity]))

(defn send-reminders
  [{:keys [time-tracker name-provider messenger planning-config]}]
  (fn []))

(defn reschedule-next-planning
  [{:keys [scheduler planning-config]}]
  (fn [new-date]
    (schedule-job scheduler send-reminders)))

(defn configure-planning
  [{:keys [scheduler planning-config]}]
  (fn []))