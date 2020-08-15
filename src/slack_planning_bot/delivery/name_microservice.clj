(ns slack-planning-bot.delivery.name-microservice
  (:require [slack-planning-bot.core.entity :as entity]))

(defrecord NameMicroserviceClient [client]
  entity/MessengerNameProvider
  (-get-user-id [this time-tracker-user-id]))

(defn make-name-ms-client
  [])
