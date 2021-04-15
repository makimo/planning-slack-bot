(ns slack-planning-bot.delivery.name-microservice
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.spec.alpha :as s]
            [environ.core :refer [env]]
            [slack-planning-bot.core.entity :as entity]
            [slack-planning-bot.core.spec :as cs]))

(defn- response
  [url id]
  (client/get (str url "slack?jira=" id)))

(defn- handle-response
  [url id]
  (try (response url id)
       (catch Exception e
         (let [err (-> e
                       Throwable->map
                       :via
                       first
                       :data
                       :status)]
           (case err
             400 :server-error
             404 :bad-request
             500 :unauthorized
             :unexpected)))))

(defn- get-user-id
  [url time-tracker-user-id]
  (if (s/valid? ::cs/message time-tracker-user-id)
    (let [response (handle-response url time-tracker-user-id)]
      (if (not (keyword? response))
        (-> response
            :body
            json/read-str
            (get "result"))
        response))
    :id-not-valid))

(defrecord NameMicroserviceClient [url]
  entity/MessengerNameProvider
  (-get-user-id [_ time-tracker-user-id]
    (get-user-id url time-tracker-user-id)))

(defn make-name-ms-client [url]
  (->NameMicroserviceClient url))