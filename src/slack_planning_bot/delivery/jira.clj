(ns slack-planning-bot.delivery.jira
  (:require [slack-planning-bot.core.entity :as entity]
            [clj-http.client :as client]
            [cheshire.core :as json]
            [slack-planning-bot.core.entity :as entity]))

(defn get-worklogs-list
  "Gets a list of worklogs starting from the date specified in the argument.
   Limit of 1000 lines per query."
  [start-date-or-path [jira-email jira-host jira-token :as auth]]
  (let [headers {:as :json
                 :basic-auth [jira-email jira-token]}
        url      (if (string? start-date-or-path)
                   start-date-or-path
                   (str jira-host
                        "/rest/api/3/worklog/updated?since="
                        (long (* start-date-or-path 1000))))
        resp      (client/get url (merge headers {:response-interceptor
                                                  (fn [resp ctx])}))
        body      (:body resp)
        worklogs  (:values body)
        next-page (:nextPage body)]
    (if (string? next-page)
      (concat worklogs (get-worklogs-list next-page auth))
      worklogs)))

(defn filtered-worklogs-list
  "Filtered list of logs due to end date."
  [start-date end-date auth]
  (filter
    #(<= (/ (:updatedTime %) 1000) end-date) (get-worklogs-list start-date
                                                                auth)))

(defn worklogs-ids
  "Id list extracted from filtered worklogs list."
  [start-date end-date auth]
  (let [filtered (filtered-worklogs-list start-date end-date auth)]
       (vec (map #(:worklogId %) filtered))))

(defn get-current-worklogs
  "Get current worklogs"
  [worklogs-ids [jira-email jira-host jira-token]]
  (flatten
    (for [ids (partition-all 1000 worklogs-ids)]
      (:body (client/post
               (str jira-host "/rest/api/3/worklog/list")
               {:as :json
                :basic-auth [jira-email jira-token]
                :body (json/generate-string {:ids ids})
                :content-type :json
                :accept :json})))))

(defn get-current-worklogs-details
  "Gets the details of the working logs, based on an id list."
  [start-date end-date auth]
  (get-current-worklogs (worklogs-ids start-date end-date auth) auth))

(defn add
  [a b]
  (+ (or a 0) b))

(defn get-account-id-time-spent
  "Sums up each user's logged in time"
  [acc {:keys [timeSpentSeconds] {:keys [accountId]} :author}]
  (update acc accountId add timeSpentSeconds))

(defn get-logged-time
  "Returns a map where the keys are user IDs
  and the values are the login time in seconds."
  [start-date end-date auth]
  (let [details (get-current-worklogs-details start-date end-date auth)]
    (reduce-kv (fn [m k v] (assoc m (keyword k) v)) {}
      (reduce get-account-id-time-spent {} details))))

(defrecord JiraClient
  [auth]
  entity/TimeTracker
  (-get-logged-time [_ start-date end-date]
    (get-logged-time start-date end-date auth)))

(defn make-jira-client [auth] (->JiraClient auth))