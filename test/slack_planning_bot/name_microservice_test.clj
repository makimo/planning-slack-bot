(ns slack-planning-bot.name-microservice-test
  (:require [clojure.data.json :as json]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [slack-planning-bot.core.entity :as entity]
            [slack-planning-bot.core.spec :as cs]
            [slack-planning-bot.delivery.name-microservice :as ms]))

(deftest get-user-id-not-string
  (let [client (ms/->NameMicroserviceClient)]
    (is (= :id-not-valid
           (entity/-get-user-id client (gen/generate (s/gen ::cs/int)))))))

(deftest get-user-id-empty-string
  (let [client (ms/->NameMicroserviceClient)]
    (is (= nil
           (entity/-get-user-id client "")))))

(deftest correct-value-handle-response
  (let [client (ms/->NameMicroserviceClient)
        str (gen/generate (s/gen ::cs/message))]
    (is (= str
           (with-redefs [ms/response (fn [url] {:status 200, :body (json/write-str {:result str})})]
             (entity/-get-user-id client str))))))

(deftest get-user-id-server-error
  (let [client (ms/->NameMicroserviceClient)]
    (is (= :server-error
           (with-redefs [ms/handle-response (fn [url] :server-error)]
             (entity/-get-user-id client (gen/generate (s/gen ::cs/message))))))))

(deftest get-user-id-random-error
  (let [client (ms/->NameMicroserviceClient)
        str (gen/generate (s/gen ::cs/message))
        keyword (gen/generate (s/gen ::cs/keyword))]
    (is (= keyword
           (with-redefs [ms/handle-response (fn [url] keyword)]
             (entity/-get-user-id client str))))))

(deftest handle-response-200
  (let [handle-response #'ms/handle-response
        json (json/write-str {:result (gen/generate (s/gen ::cs/message))})]
    (is (= {:status 200, :body json}
           (with-redefs [ms/response (fn [url] {:status 200, :body json})]
             (handle-response (gen/generate (s/gen ::cs/message))))))))

(deftest handle-response-400
  (let [handle-response #'ms/handle-response]
    (is (= :server-error
           (with-redefs [ms/response (fn [url] (throw (ex-info "400" {:status 400})))]
             (handle-response (gen/generate (s/gen ::cs/message))))))))

(deftest handle-response-404
  (let [handle-response #'ms/handle-response]
    (is (= :bad-request
           (with-redefs [ms/response (fn [url] (throw (ex-info "404" {:status 404})))]
             (handle-response (gen/generate (s/gen ::cs/message))))))))

(deftest handle-response-500
  (let [handle-response #'ms/handle-response]
    (is (= :unauthorized
           (with-redefs [ms/response (fn [url] (throw (ex-info "500" {:status 500})))]
             (handle-response (gen/generate (s/gen ::cs/message))))))))

(deftest handle-unexcepted-error
  (let [handle-response #'ms/handle-response]
    (is (= :unexpected
           (with-redefs [ms/response (fn [url] (throw (ex-info "Unexpected error" {:status 402})))]
             (handle-response (gen/generate (s/gen ::cs/message))))))))
