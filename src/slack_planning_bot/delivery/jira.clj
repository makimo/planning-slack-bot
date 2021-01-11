(ns slack-planning-bot.delivery.jira
  (:require [slack-planning-bot.core.entity :as entity]
            [clj-http.client                :as client]
            [cheshire.core                  :as json]
            [environ.core                   :refer [env]]))

(defn get-worklogs-list
  "Gets a list of worklogs starting from the date specified in the argument.
   Limit of 1000 lines per query."
  [start-date]
  (client/get
    (str (env :jira-host) "/rest/api/3/worklog/updated?since=" start-date)
    {:as :json
     :basic-auth [(env :jira-email)
                  (env :jira-token)]}))

(defn filtered-worklogs-list
  "Filtered list of logs due to end date."
  [start-date end-date]
  (filter
    #(<= (:updatedTime %) end-date) (get-in (get-worklogs-list start-date) [:body :values])))

(defn worklogs-ids
  "Id list extracted from filtered worklogs list."
  [start-date end-date]
  (vec (map #(:worklogId %) (filtered-worklogs-list start-date end-date))))

(defn get-current-worklogs
  "Get current worklogs"
  [worklogs-ids]
  (client/post
    (str (env :jira-host) "/rest/api/3/worklog/list")
    {:as :json
     :basic-auth [(env :jira-email)
                  (env :jira-token)]
     :body (json/generate-string {:ids worklogs-ids})
     :content-type :json
     :accept :json}))

(defn get-current-worklogs-details
  "Gets the details of the working logs, based on an id list."
  [start-date end-date]
  (get-in (get-current-worklogs (worklogs-ids start-date end-date)) [:body]))

(defn add
  [a b]
  (+ (or a 0) b))

(defn get-account-id-time-spent
  "Sums up each user's logged in time"
  [acc {:keys [timeSpentSeconds] {:keys [accountId]} :author}]
  (update acc accountId add timeSpentSeconds))

(defn list-all-user-logged-time
  "Returns a map where the keys are user IDs
  and the values are the login time in seconds."
  [start-date end-date]
  (reduce-kv (fn [m k v] (assoc m (keyword k) v)) {}
    (reduce get-account-id-time-spent {}
      (get-current-worklogs-details start-date end-date))))

(defn get-logged-time-all-pages
  ""
  [start-date end-date]
    (loop [date start-date]
      (println (list-all-user-logged-time date end-date))
      (when (= (get-in (get-worklogs-list date) [:body :lastPage]) false)
        (recur (+ (- date date) (+ (get-in (get-worklogs-list date) [:body :until]) 1))))))

(defn get-logged-time
  ""
  [start-date end-date]
  (if (= (get-in (get-worklogs-list start-date) [:body :lastPage]) false)
    (list-all-user-logged-time start-date end-date)
    (get-logged-time-all-pages start-date end-date)))

(defrecord JiraClient []
  entity/TimeTracker
  (-get-logged-time [_ start-date end-date]
    (get-logged-time start-date end-date)))

(defn make-jira-client [] (->JiraClient))