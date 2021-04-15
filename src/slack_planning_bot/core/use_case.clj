(ns slack-planning-bot.core.use-case
  (:require [com.walmartlabs.cond-let :refer [cond-let]]
            [slack-planning-bot.core.entity :as e]
            [slack-planning-bot.core.utils :as utils]
            [slack-planning-bot.core.action :as action]
            [clj-time.core :as t]))

(defn- result->action
  [type result]
  (cond-let
    :let [payload {:result result}]

    (e/messenger-error? result)
    (action/make-error type :messenger payload)

    (e/time-tracker-error? result)
    (action/make-error type :time-tracker payload)

    (e/name-provider-error? result)
    (action/make-action type payload)

    (e/scheduler-error? result)
    (action/make-error type :scheduler payload)

    :else (action/make-action type payload)))

(defn- update-planning-config
  [config interval day]
  (swap! config assoc :interval interval
    :day-of-week day
    :next-planning
    (e/calculate-next-planning (t/now) interval day)))

(defn- update-next-planning
  [config next]
  (swap! config assoc :next-planning next))

(defn send-message
  [messenger user-id time]
  (let [msg (str "Od ostatniego planningu zalogowałeś/aś " (double (/ time 3600))
                 " godzin. Upewnij się że zalogowałeś/aś wszystko.")]
    (result->action :e/send-reminder
      (e/send-message-to-user messenger (hash-map user-id msg)))))

(defn- properly-logged-time
  "Checks if user properly logged time the benchmark is 8 hours a day
  counted from the previous interval until today (in seconds)"
  [user-logged-time start-date end-date]
       (println [user-logged-time start-date end-date])
  (>= user-logged-time
    (* (* (utils/count-work-days start-date end-date) 8) 3600)))

(defn message-users
  [tracker provider messenger interval users]
  (let [start-date        (t/minus (t/now) (t/days interval))
        end-date          (t/now)
        start-date-unix   (/ (.getMillis start-date) 1000)
        end-date-unix     (/ (.getMillis end-date) 1000)
        users-logged-time (e/get-logged-time tracker
                                             start-date-unix
                                             end-date-unix)]
    (for [user users]
       (let [time (get users-logged-time (keyword (second user)) 0)]
       (if-not (properly-logged-time time start-date end-date)
          (do
            (println "Attempting to send a message to " user)
            (send-message messenger (first user) time)))))))

(defn send-reminders
  "Schedule its own activity (always one day before planning)
  and send reminders to users if needed"
  [scheduler time-tracker name-provider messenger planning-config]
  (let [int       (get @planning-config :interval)
        day       (get @planning-config :day-of-week)
        users     (get @planning-config :users)
        calc-next (e/calculate-next-planning (t/now) int day)]
    (println int day calc-next users)
    (println (message-users time-tracker name-provider messenger int users))
    (update-next-planning planning-config calc-next)
    (->> calc-next
      (e/schedule-job scheduler
        #(send-reminders
           scheduler
           time-tracker
           name-provider
           messenger
           planning-config))
      (result->action :e/schedule-planning))))

(defn- reschedule-planning
  [next scheduler tracker provider messenger config]
  (update-next-planning config next)
   (->> next
     (e/schedule-job scheduler
       #(send-reminders
          scheduler
          tracker
          provider
          messenger
          config))
     (result->action :e/configure-planning)))

(defn configure-planning
  "There are 8 input possibilities (next-planning, interval, day-of-week),
  where 4 of them (000, 101, 110 and 111) are invalid"
  [scheduler time-tracker name-provider messenger planning-config
   {next :next-planning int :interval day :day-of-week}]
  (cond
    (or (and next (or int day)) (not (or next int day)))
    (action/make-error :e/configure-planning :invalid-arguments)
    next
    (reschedule-planning next scheduler time-tracker name-provider messenger planning-config)
    (not= (rem int 7) 0)
    (action/make-error :e/configure-planning :invalid-interval)
    :else (update-planning-config planning-config int day)))