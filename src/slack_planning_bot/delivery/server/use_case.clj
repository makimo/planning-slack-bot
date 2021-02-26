(ns slack-planning-bot.delivery.server.use-case
  (:require [cheshire.core :as json]
            [clojure.spec.alpha :as s]
            [slack-planning-bot.delivery.server.http :as http]
            [slack-planning-bot.delivery.server.spec :as spec]
            [slack-planning-bot.delivery.state :as state]
            [slack-planning-bot.delivery.server.utils :as utils]))

(defn- configure-planning-bot-next-planning [date-string]
  (let [next-planning (utils/date-in-string->date date-string)]
    (utils/action->response (state/configure-planning {:next-planning next-planning}))))

(defn- configure-planning-bot [json-params]
  "Get parameters from json-params and run configure-planning from state."
  (let [interval (json-params :interval)
        day-of-week (json-params :day_of_week)]
    (utils/action->response (state/configure-planning {:interval    interval
                                                       :day-of-week day-of-week}))))


(defn post-configure-bot [{:keys [json-params]}]
  "Config bot POST request.
  If data is invalid return explain spec info."
  (if (s/valid? ::spec/post-configure-bot-request json-params)
    (configure-planning-bot-next-planning json-params)
    (-> json-params
        (s/explain-data ::spec/post-configure-bot-request)
        (json/encode)
        (http/bad-request))))

(defn post-next-planning-date [{:keys [json-params]}]
  "Config bot POST request.
  If data is invalid return explain spec info."
  (if (s/valid? ::spec/post-next-planning-date-request json-params)
    (configure-planning-bot json-params)
    (-> json-params
        (s/explain-data ::spec/post-next-planning-date-request)
        (json/encode)
        (http/bad-request))))