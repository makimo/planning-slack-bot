(ns slack-planning-bot.core.entity)

(defprotocol Messenger
  "Abstract messenger protocol. Gives ability to send messages to users."
  (-send-messages-to-users [this user-id->message]
    "Send messages to users. Expects map of the form: {<user-id>: <message>}"))

(defprotocol TimeTracker
  "Abstract time-tracker protocol. Gives ability to get amount of
  logged time for users."
  (-get-logged-time [this start-date end-date]))

(defprotocol MessengerNameProvider
  "Abstract name-provider for messenger, exchanges user-ids."
  (-get-user-id
    [this time-tracker-user-id]
    "Given user-id for time-tracker (e.g. Jira), get user-id
    for messenger service (e.g. Slack)."))

(defprotocol Scheduler
  "Abstract job scheduler.
  Ensures that job-fn will be run at specified date.
  By design, it supports only job at a time - when scheduling,
  previous job is automatically unscheduled."
  (-schedule-job [this job-fn date])
  (-cancel-job [this]))

(defn send-message-to-user [messenger user-id->message]
  (-send-messages-to-users messenger user-id->message))

(defn get-logged-time [tracker user-id start-date end-date]
  (-get-logged-time tracker start-date end-date))

(defn get-user-id [provider time-tracker-user-id]
  (-get-user-id provider time-tracker-user-id))

(defn schedule-job [scheduler job-fn date]
  (-schedule-job scheduler job-fn date))

(defn cancel-job [scheduler]
  (-cancel-job scheduler))

(defn calculate-next-planning
  "Given minimal interval period, day-of-week on which
  planning take place and current date, calculates new planning date.
  Next planning must be no sooner than next week."
  [current-date interval day-of-week])