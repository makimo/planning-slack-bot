(ns slack-planning-bot.delivery.server.utils
  (:require [cheshire.core :as json]
            [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clojure.string :as str]
            [io.pedestal.http :as bootstrap]
            [io.pedestal.http.body-params :as body-params]
            [slack-planning-bot.core.action :as action]
            [slack-planning-bot.delivery.server.http :as http]))

(def time-formatter
  "Formatter for joda date."
  (f/formatter "dd-MM-YYYY"))

(defn date-in-string->date
  "Convert date in string format to java.util.Date"
  [date-in-string]
  (c/to-date (f/parse time-formatter date-in-string)))

(defn- date-in-string->local-date
  "Convert date in string format to org.joda.time.LocalDate"
  [date-in-string]
  (->> (str/split date-in-string #"[-]")
       (reverse)
       (map read-string)
       (apply t/local-date)))

(defn- merge-with-payload->json
  "Merge coll with payload and return in json."
  [coll payload]
  (json/encode (merge payload coll)))

(defn inject-interceptors [service-map]
  (-> service-map
      bootstrap/default-interceptors
      (update ::bootstrap/interceptors conj (body-params/body-params))
      (update ::bootstrap/interceptors conj bootstrap/html-body)))

; TODO left for validating date in core module
(defn valid-date?
  "Validate date in proper formats:
   1) dd-MM-YYYY, e.g. 02-10-2020
   2) dd/MM/YYYY, e.g. 12/10/2020
   Returns true if specified data is in proper format
   and it's after or equal to current date."
  [date-string]
  (try
    (f/parse time-formatter date-string)
    (let [next-planning-date (date-in-string->local-date date-string)
          current-date (t/today)]
      (cond
        (t/equal? next-planning-date current-date)
        true
        (t/after? next-planning-date current-date)
        true
        :else nil))
    (catch Exception _ nil)))

; TODO check if working after merging Ela PR
(defn action->response
  "Converts an action to a response."
  [{:keys [:error :payload]}]
  (cond
    (= error :invalid-arguments)
    (http/bad-request (json/encode {:error "Invalid arguments provided."}))
    (= error :invalid-interval)
    (http/bad-request (json/encode {:error "Invalid interval."}))
    (= error :messenger)
    (http/bad-request (json/encode {:error   "Error occurred on sending messages to users."
                                    :payload payload}))
    (= error :time-tracker)
    (http/bad-request (json/encode {:error   "Error occurred on getting the amount of logged time for users."
                                    :payload payload}))
    (= error :name-provider)
    (http/bad-request (json/encode {:error   "Error occurred on exchanging user-ids."
                                    :payload payload}))
    (= error :scheduler)
    (http/bad-request (json/encode {:error "Scheduler result was an error." :payload payload}))
    (nil? error)
    (http/ok (merge-with-payload->json {:error nil} payload))))
