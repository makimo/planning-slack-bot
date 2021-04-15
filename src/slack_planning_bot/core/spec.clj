(ns slack-planning-bot.core.spec
  (:require [slack-planning-bot.core.entity :as entity]
            [slack-planning-bot.core.use-case :as uc]
            [clojure.spec.alpha :as s])
  (:import (clojure.lang Atom)))

(s/def ::user-id string?)
(s/def ::message string?)
(s/def ::time int?)
(s/def ::date inst?)
(s/def ::user-id->message map?)
(s/def ::int int?)
(s/def ::keyword keyword?)

(s/def ::messenger #(satisfies? entity/Messenger %))
(s/def ::messenger-error keyword?)
(s/def ::messenger-result (s/or :data boolean? :error ::messenger-error))

(s/def ::time-tracker #(satisfies? entity/TimeTracker %))
(s/def ::time-tracker-error keyword?)
(s/def ::time-tracker-result (s/or :data ::date :error ::time-tracker-error))

(s/def ::name-provider #(satisfies? entity/MessengerNameProvider %))
(s/def ::name-provider-error keyword?)
(s/def ::name-provider-result (s/or :data :user-id :error ::name-provider-error))

(s/def ::scheduler #(satisfies? entity/Scheduler %))
(s/def ::scheduler-error keyword?)
(s/def ::scheduler-result (s/or :data ::date :error ::scheduler-error))

(s/def ::planning-config #(instance? Atom %))

(s/fdef entity/send-message-to-user
  :args (s/cat :messenger ::messenger :user-id->message ::user-id->message)
  :ret boolean?)

(s/fdef entity/get-logged-time
  :args (s/cat :time-tracker ::time-tracker :user-id ::user-id
          :start-date ::start-date :end-date ::end-date)
  :ret ::time-tracker-result)

(s/fdef entity/get-user-id
  :args (s/cat :name-provider ::name-provider :time-tracker-user-id
          ::time-tracker-user-id)
  :ret ::name-provider-result)

(s/fdef entity/schedule-job
  :args (s/cat :scheduler ::scheduler :job-fn ::job-fn :date ::date)
  :ret ::scheduler-result)

(s/fdef entity/cancel-job
  :args (s/cat :scheduler ::scheduler)
  :ret boolean?)

(s/fdef entity/calculate-next-planning
  :args (s/cat :interval ::interval :day-of-week ::day-of-week)
  :ret inst?)

(s/fdef entity/messenger-error?
  :args (s/cat :result ::messenger-result)
  :ret boolean?)

(s/fdef entity/time-tracker-error?
  :args (s/cat :result ::time-tracker-result)
  :ret boolean?)

(s/fdef entity/name-provider-error?
  :args (s/cat :result ::name-provider-result)
  :ret boolean?)

(s/fdef entity/scheduler-error?
  :args (s/cat :result ::scheduler-result)
  :ret boolean?)

(s/fdef uc/configure-planning
  :args (s/cat :scheduler ::scheduler
          :time-tracker ::time-tracker
          :name-provider ::name-provider
          :messenger ::messenger
          :planning-config ::planning-config
          :args map?)
  :ret (s/or :planning-config ::planning-config :error map?))

(s/fdef uc/send-reminders
  :args (s/cat :scheduler ::scheduler
          :time-tracker ::time-tracker
          :name-provider ::name-provider
          :messenger ::messenger
          :planning-config ::planning-config)
  :ret map?)