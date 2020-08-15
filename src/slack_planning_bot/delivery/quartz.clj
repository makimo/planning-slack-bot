(ns slack-planning-bot.delivery.quartz
  (:require [slack-planning-bot.core.entity :as entity]
            [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.jobs :refer [defjob]]
            [clojurewerkz.quartzite.schedule.simple :as s]))

(defmacro def- [item value] `(def ^:private ~item ~value))

(def- trigger-key (t/key "planning.job.trigger"))
(def- job-key (j/key "planning.job"))
(def- planning-job-fn (atom (fn [])))
(defjob PlanningJob [c] (@planning-job-fn))

(defn- create-next-trigger
  "Create trigger for the next planning"
  [date]
  (t/build
    (t/with-identity trigger-key)
    (t/start-at date)))

(defn- cancel-job
  [client]
  (qs/delete-trigger client trigger-key))

(defn- schedule-job
  [client job-fn date]
  (cancel-job client)
  (reset! planning-job-fn job-fn)
  (let [trigger (create-next-trigger date)
        job (j/build (j/of-type PlanningJob)
                     (j/with-identity job-key))]
    (qs/schedule client job trigger)))

(defrecord QuartzScheduler [client]
  entity/Scheduler
  (-schedule-job [_ job-fn date] (schedule-job client job-fn date))
  (-cancel-job [_] (cancel-job client)))

(defn make-quartz-scheduler
  []
  (->QuartzScheduler (qs/start (qs/initialize))))
