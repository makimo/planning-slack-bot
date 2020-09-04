(ns slack-planning-bot.utils
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [slack-planning-bot.core.entity :as e]
            [clojure.test.check.generators :as gen]
            [slack-planning-bot.core.spec :as spec]
            [slack-planning-bot.core.action :as action]
            [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.jobs :refer [defjob]]
            [clojurewerkz.quartzite.jobs :as j])
  (:import (java.util UUID)))

(def ^:private trigger-key (t/key "planning.job.trigger"))
(def ^:private job-key (j/key "planning.job"))
(def ^:private planning-job-fn (atom (fn [])))
(defjob PlanningJob [c] (@planning-job-fn))

(def ^:private get-logged-time gen/nat)
(def ^:private get-user-id (str (UUID/randomUUID)))

(defn- cancel-job [client]
  (qs/delete-trigger client trigger-key))

(defn- create-next-trigger
  "Create trigger for the next planning"
  [date]
  (t/build
    (t/with-identity trigger-key)
    (t/start-at date)))

(defn- schedule-job
  [client job-fn date]
  (cancel-job client)
  (reset! planning-job-fn job-fn)
  (let [trigger (create-next-trigger date)
        job     (j/build (j/of-type PlanningJob)
                  (j/with-identity job-key))]
    (qs/schedule client job trigger)))

(def gen-date (gen/generate (s/gen ::spec/date)))

(def gen-invalid-interval
  (gen/generate
    (gen/such-that #(not= (rem % 7) 0)
      gen/small-integer)))

(def invalid-arguments-error
  (action/make-error :e/configure-planning :invalid-arguments))

(def invalid-interval-error
  (action/make-error :e/configure-planning :invalid-interval))

(defrecord TestScheduler [client]
  e/Scheduler
  (-schedule-job [_ job-fn date] (schedule-job client job-fn date))
  (-cancel-job [_] (cancel-job client)))

(defn init-scheduler []
  (->TestScheduler (qs/start (qs/initialize))))

(defrecord TestTimeTracker [token]
  e/TimeTracker
  (-get-logged-time [_ user-id- start-date end-date] get-logged-time))

(defrecord TestNameProvider []
  e/MessengerNameProvider
  (-get-user-id [_ time-tracker-user-id] get-user-id))

(defrecord TestMessenger [token]
  e/Messenger
  (-send-messages-to-users [_ user-id->message] true))