(ns slack-planning-bot.core
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.jobs :refer [defjob]]
            [clojurewerkz.quartzite.schedule.simple :as s]))

(defjob NoOpJob
  [ctx]
  (println "x")
  (comment "Does nothing"))

(def tk (t/key "triggers.1"))

(defn -main
  [& m]
  (let [s (-> (qs/initialize) qs/start)
        job (j/build
              (j/of-type NoOpJob)
              (j/with-identity (j/key "jobs.noop.1")))

        trigger (t/build
                  (t/start-now)
                  )]
    ;; submit for execution
    (qs/schedule s job trigger)
    ;; and immediately unschedule the trigger
    ))

(def trigger-key (t/key "planning.job.trigger"))

(defjob PlanningJob
  [ctx]
  ; 1. Send reminders
  ; 2. Calculate next planning date
  ; (Next planning cannot happen sooner than next week)
  ; 3. Schedule next job and update state
  )

(defn reschedule-next-planning
  ; 1. Unschedule next planning job
  ; 2. Schedule new date and update state
  )

(defn- calculate-next-planning
  "Returns the date of next planning."
  )

(defn- create-next-trigger
  "Create trigger for the next planning"
  []
  (let [next-date (calculate-next-planning)]
    (t/build
      (t/with-identity trigger-key)
      (t/start-at next-date))))