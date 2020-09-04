(ns slack-planning-bot.check
  (:require [clojure.test :refer :all]
            [slack-planning-bot.utils :refer :all]
            [clojure.spec.test.alpha :as stest]
            [clojure.spec.gen.alpha :as gen]
            [slack-planning-bot.core.spec :as spec]
            [slack-planning-bot.core.use-case :as uc]))

(def gen-scheduler (gen/return (init-scheduler)))
(def gen-tracker (gen/return (->TestTimeTracker "")))
(def gen-provider (gen/return (->TestNameProvider)))
(def gen-messenger (gen/return (->TestMessenger "")))
(def gen-config (gen/return (atom {:interval 7 :day-of-week 5})))

(deftest check-configure-planning
  (let [ret (-> `uc/configure-planning
              (stest/check
                {:gen {::spec/scheduler       (fn [] gen-scheduler)
                       ::spec/time-tracker    (fn [] gen-tracker)
                       ::spec/name-provider   (fn [] gen-provider)
                       ::spec/messenger       (fn [] gen-messenger)
                       ::spec/planning-config (fn [] gen-config)}})
              first
              :clojure.spec.test.check/ret)]
    (is (:pass? ret) (str ret))))

(deftest check-send-reminders
  (let [ret (-> `uc/send-reminders
              (stest/check
                {:gen {::spec/scheduler       (fn [] gen-scheduler)
                       ::spec/time-tracker    (fn [] gen-tracker)
                       ::spec/name-provider   (fn [] gen-provider)
                       ::spec/messenger       (fn [] gen-messenger)
                       ::spec/planning-config (fn [] gen-config)}})
              first
              :clojure.spec.test.check/ret)]
    (is (:pass? ret) (str ret))))