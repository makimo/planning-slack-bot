(ns slack-planning-bot.delivery.jira
  (:require [slack-planning-bot.core.entity :as entity]))

(defrecord JiraClient [client]
  entity/TimeTracker
  (get-logged-time [this user-id start-date end-date]))

(defn make-jira-client
  [token])