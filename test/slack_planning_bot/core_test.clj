(ns slack_planning_bot.core_test
  (:require [clojure.test :refer :all]
            [clojure.spec.test.alpha :as stest]
            [slack-planning-bot.core.entity :as e]
            [slack-planning-bot.delivery.state :as state]
            [slack-planning-bot.utils :refer :all]
            [mount.core :as mount]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [slack-planning-bot.core.use-case :as uc]))

(stest/instrument `uc/configure-planning)
(stest/instrument `uc/send-reminders)

(use-fixtures :each
  (fn
    [f]
    (mount/stop)
    (mount/start-with
      {#'slack-planning-bot.delivery.state/scheduler      (init-scheduler)
       #'slack-planning-bot.delivery.state/jira-client    (->TestTimeTracker "")
       #'slack-planning-bot.delivery.state/name-ms-client (->TestNameProvider)
       #'slack-planning-bot.delivery.state/slack-client   (->TestMessenger "")})
    (f)))

(deftest test-calculate-next-planning
  (testing "Correctness of calculation of the next scheduling date")
  (is (= (e/calculate-next-planning (t/date-time 2020 9 11) 7 2)
        (t/date-time 2020 9 22)))
  (is (= (e/calculate-next-planning (t/date-time 2020 9 18) 7 5)
        (t/date-time 2020 9 25)))
  (is (= (e/calculate-next-planning (t/date-time 2020 9 2) 14 4)
        (t/date-time 2020 9 17))))

(deftest test-configure-planning-invalid-arguments
  (testing "Invalid input for planning configuration.
  There are 4 invalid input possibilities of
   next-planning, interval, day-of-week, such as:
   000, 101, 110 and 111")
  (is (= invalid-arguments-error
        (state/configure-planning
          {:next-planning nil
           :interval      nil
           :day-of-week   nil})))
  (is (= invalid-arguments-error
        (state/configure-planning
          {:next-planning gen-date
           :interval      nil
           :day-of-week   5})))
  (is (= invalid-arguments-error
        (state/configure-planning
          {:next-planning gen-date
           :interval      7
           :day-of-week   nil})))
  (is (= invalid-arguments-error
        (state/configure-planning
          {:next-planning gen-date
           :interval      7
           :day-of-week   5}))))

(deftest test-configure-planning-invalid-interval
  (testing "Invalid interval input")
  (is (= invalid-interval-error
        (state/configure-planning
          {:next-planning nil
           :interval      gen-invalid-interval
           :day-of-week   5}))))

(deftest test-configure-planning-update-planning-config
  (testing "Update planning config interval and day of week")
  (let [fmt (f/formatters :year-month-day)
        int 7 day 5]
    (state/configure-planning
      {:next-planning nil
       :interval      int
       :day-of-week   day})
    (is (= (get @state/planning-config :interval) int))
    (is (= (get @state/planning-config :day-of-week) day))
    (is (= (f/unparse fmt (get @state/planning-config :next-planning))
          (f/unparse fmt (e/calculate-next-planning (t/now) int day))))))

(deftest test-configure-planning-reschedule-planning
  (testing "Reschedule next planing")
  (let [next gen-date]
    (state/configure-planning
      {:next-planning next
       :interval      nil
       :day-of-week   nil})
    (is (= (get @state/planning-config :next-planning) next))))

(deftest test-send-reminders
  (testing "Sending reminders result action")
  (is (= :e/schedule-planning
        (get (state/send-reminders)
          :slack-planning-bot.core.action/type)))
  (is (= nil (get (state/send-reminders)
               :slack-planning-bot.core.action/error))))