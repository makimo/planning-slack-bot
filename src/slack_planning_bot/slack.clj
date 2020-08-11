(ns slack-planning-bot.slack
  "Slack client for Clojure. Wraps Slack Java SDK. Example usage:

    (let [token 'xoxb-4089195133-1292908442416-pJ1MP6q4pbvqqNkaOyEjN8lW'
          client (get-slack-client token)
          users (fetch-users client)]
         ;; Send message to all users.
      (send-message-to-users client users 'hello'))"
  (:require [environ.core :refer [env]])
  (:import (com.slack.api Slack)
           (com.slack.api.methods.request.chat ChatPostMessageRequest)
           (com.slack.api.methods.request.conversations ConversationsOpenRequest)
           (com.slack.api.methods.request.users UsersListRequest)))

(defn is-error?
  "Check if response is an error."
  [response]
  (keyword? response))

(defn response->error
  [response]
  (keyword (.getError response)))

(defn get-channel
  "Build Channel instance, given its ID."
  [id]
  (.channel (ChatPostMessageRequest/builder) id))

(defn get-slack-client
  "Build Slack client instance, given authorization token.
  If no token is specified, default one is loaded from the environment.
  All Slack functions require Slack client instance."
  ([]
   (.methods (Slack/getInstance) (env :slack-token)))
  ([token]
   (.methods (Slack/getInstance) token)))

(defn user->map
  "Convert User instance to plain map."
  [user]
  ;; Extract more fields as needed.
  {:id         (.getId user)
   :name       (.getName user)
   :is-deleted (.isDeleted user)
   :is-bot     (.isBot user)})

(defn fetch-users
  "Fetch Slack users (see user->map for schema)."
  [client]
  (let [response (.usersList client (.build (UsersListRequest/builder)))]
    (if (.isOk response)
      (->> response
           .getMembers
           (map user->map)
           (remove #(or (:is-bot %) (:is-deleted %)))
           (map #(dissoc % :is-bot :is-deleted))
           set))))

(defn open-conversation
  "Create or re-join conversation with specified users.
  Returns channel ID (string) or error keyword."
  [client users]
  (let [request (.build (.users (ConversationsOpenRequest/builder) users))
        response (.conversationsOpen client request)]
    (if (.isOk response)
      (.getId (.getChannel response))
      (response->error response))))

(defn send-message
  "Send message to specified channel."
  [client channel-id message]
  (let [request (.build (.text (get-channel channel-id) message))
        response (.chatPostMessage client request)]
    (if-not (.isOk response)
      (response->error response))))

(defn send-message-to-users
  "Send message to specified users.
  Accept both set of user-maps or list of user-ids."
  [client users message]
  (let [user-ids (if (string? (take 1 users)) users (map :id users))
        conv-id (open-conversation client user-ids)]
    (send-message client conv-id message)))