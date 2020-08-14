(ns slack-planning-bot.core.entity)

(defprotocol Messenger
  (-send-message-to-user [this user-id message]))

(defprotocol TimeTracker
  (-get-logged-time [this user-id start-date end-date]))

(defprotocol MessengerNameProvider
  (-get-user-id [this time-tracker-user-id]))

(defprotocol Scheduler
  (-schedule-job [this job-fn date])
  (-cancel-job [this]))

(defn send-message-to-user [messenger user-id message]
  (-send-message-to-user messenger user-id message))

(defn get-logged-time [tracker user-id start-date end-date]
  (-get-logged-time tracker user-id start-date end-date))

(defn get-user-id [provider time-tracker-user-id]
  (-get-user-id provider time-tracker-user-id))

(defn schedule-job [scheduler job-fn date]
  (-schedule-job scheduler job-fn date))

(defn cancel-job [scheduler]
  (-cancel-job scheduler))

(defn calculate-next-planning
  [current-date])

(defn schedule-next-planning
  [current-date scheduler planning-config])