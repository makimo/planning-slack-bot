(ns slack-planning-bot.api-test
  (:require [cheshire.core :as json]
            [clojure.test :refer :all]
            [io.pedestal.http :as bootstrap]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :refer :all]
            [slack-planning-bot.delivery.server.service :as service]
            [slack-planning-bot.delivery.server.utils :refer [inject-interceptors]]
            [clojure.spec.gen.alpha :as gen])
  (:import (java.time LocalDate)
           (java.time.temporal ChronoField)))


(def url-for
  (route/url-for-routes (route/expand-routes service/routes)))

(def service
  (::bootstrap/service-fn (-> service/service
                              inject-interceptors
                              bootstrap/create-servlet)))
;generator for dates
(def gen-local-date-str
  (let [day-range (.range (ChronoField/EPOCH_DAY))
        day-min (.getMinimum day-range)
        day-max (.getMaximum day-range)]
    (gen/fmap #(str (LocalDate/ofEpochDay %))
              (gen/large-integer* {:min day-min :max day-max}))))

(def headers {"Content-Type" "application/json"})


(deftest test-configure-bot-returns-400-when-params-are-not-given
  (is (= 400 (:status (response-for service
                                    :post (url-for :post-configure-bot)
                                    :headers headers
                                    :body (json/encode {}))))))

(deftest test-next-planning-date-returns-400-when-params-are-not-given
  (is (= 400 (:status (response-for service
                                    :post (url-for :post-next-planning-date)
                                    :headers headers
                                    :body (json/encode {}))))))

(deftest test-configure-bot-returns-400-when-interval-day-is-string
  (is (= 400 (:status (response-for service
                                    :post (url-for :post-configure-bot)
                                    :headers headers
                                    :body (json/encode {:interval "12"}))))))

(deftest test-configure-bot-returns-400-when-day-of-the-week-is-out-of-range
  (is (= 400 (:status (response-for service
                                    :post (url-for :post-configure-bot)
                                    :headers headers
                                    :body (json/encode {:day_of_week 8}))))))

(deftest test-configure-bot-returns-400-when-both-params-are-given-but-interval-day-is-string
  (is (= 400 (:status (response-for service
                                    :post (url-for :post-configure-bot)
                                    :headers headers
                                    :body (json/encode {:day_of_week 3 :interval "12"}))))))

(deftest test-next-planning-date-returns-400-when-planning-date-has-invalid-type
  (is (= 400 (:status (response-for service
                                    :post (url-for :post-next-planning-date)
                                    :headers headers
                                    :body (json/encode {:next-planning 2}))))))

; TODO Add tests after merging Ela PR
;(deftest test-next-planning-date-returns-400-when-planning-date-is-valid
;  (is (= 200 (:status (response-for service
;                                    :post (url-for :post-next-planning-date)
;                                    :headers headers
;                                    :body (json/encode {:next-planning (gen/generate gen-local-date-str)}))))))




