(ns slack-planning-bot.delivery.quartz
  (:require [slack-planning-bot.core.entity :as entity]
            [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.jobs :refer [defjob]]
            [clojurewerkz.quartzite.schedule.simple :as s]))

(def trigger-key (t/key "planning.job.trigger"))

(defn- create-next-trigger
  "Create trigger for the next planning"
  [date]
  (t/build
    (t/with-identity trigger-key)
    (t/start-at date)))

(defn- cancel-job
  [])

(defn- schedule-job
  [client job-fn date]
  (cancel-job)
  (let [trigger (create-next-trigger date)]
    ))

(defrecord QuartzScheduler [client]
  entity/Scheduler
  (schedule-job [this job-fn date] (schedule-job client job-fn date))
  (cancel-job [this]))

(defn make-quartz-scheduler
  []
  (->QuartzScheduler (qs/start (qs/initialize))))