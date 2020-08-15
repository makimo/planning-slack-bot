(ns slack-planning-bot.delivery.state
  (:require [slack-planning-bot.core.use-case :as uc]
            [slack-planning-bot.delivery.slack :refer [make-slack-client]]
            [slack-planning-bot.delivery.jira :refer [make-jira-client]]
            [slack-planning-bot.delivery.quartz :refer [make-quartz-scheduler]]
            [slack-planning-bot.delivery.name-microservice :refer [make-name-ms-client]]
            [mount.core :refer [defstate]]))

(defstate slack-client   :start (make-slack-client "..."))
(defstate jira-client    :start (make-jira-client "..."))
(defstate scheduler      :start (make-quartz-scheduler))
(defstate name-ms-client :start (make-name-ms-client))

(defstate planning-config
          "Internal state of the app.
          Stores information about current config and next planning."
          :start (atom {:next-planning nil
                        :day-of-week 1
                        :interval 14}))

(defstate send-reminders
          :start (uc/send-reminders
                   {:time-tracker jira-client
                    :name-provider make-name-ms-client
                    :messenger slack-client
                    :planning-config planning-config}))

(defstate configure-planning
          :start (uc/configure-planning
                   {:scheduler scheduler
                    :planning-config planning-config}))

