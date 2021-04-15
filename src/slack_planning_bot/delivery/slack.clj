(ns slack-planning-bot.delivery.slack
  "Slack client for Clojure. Wraps Slack Java SDK. Example usage:

    (let [token 'xoxb-...'
          client (get-slack-client token)
          users (fetch-users client)]
         ;; Send message to all users.
      (send-message-to-users client users 'hello'))"
  (:require [slack-planning-bot.core.entity :as entity])
  (:import (com.slack.api Slack)
           (com.slack.api.methods.request.chat ChatPostMessageRequest)
           (com.slack.api.methods.request.conversations ConversationsOpenRequest)
           (com.slack.api.methods.request.users UsersListRequest)))

(defn is-error?
  "Check if response is an error."
  [response]
  (keyword? response))

(defn- response->error
  [response]
  (keyword (.getError response)))

(defn- get-channel
  "Build Channel instance, given its ID."
  [id]
  (.channel (ChatPostMessageRequest/builder) id))

(defn- get-slack-client
  "Build Slack client instance, given authorization token.
  If no token is specified, default one is loaded from the environment.
  All Slack functions require Slack client instance."
  [token]
  (.methods (Slack/getInstance) token))

(defn- user->map
  "Convert User instance to plain map."
  [user]
  ;; Extract more fields as needed.
  {:id         (.getId user)
   :name       (.getName user)
   :is-deleted (.isDeleted user)
   :is-bot     (.isBot user)})

(defn- fetch-users
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

(defn- open-conversation
  "Create or re-join conversation with specified users.
  Returns channel ID (string) or error keyword."
  [client users]
  (let [request (.build (.users (ConversationsOpenRequest/builder) users))
        response (.conversationsOpen client request)]
    (if (.isOk response)
      (.getId (.getChannel response))
      (response->error response))))

(defn- send-message
  "Send message to specified channel."
  [client channel-id message]
  (let [request (.build (.text (get-channel channel-id) message))
        response (.chatPostMessage client request)]
    (if-not (.isOk response)
      (response->error response))))

(defn- send-message-to-user
  "Send message to specified users.
  Accept both set of user-maps or list of user-ids.
  User ID for this function is an internal Slack ID, e.g. 'U01BJDKYPQ'."
  [client user message]
  (let [id-or-error (open-conversation client [user])]
    (if (is-error? id-or-error)
      id-or-error
      (send-message client id-or-error message))))

(defn- send-messages-to-users
  [client slack-username->message]
  (let [usernames (set (keys slack-username->message))
        name->id (->> (fetch-users client)
                      (filter #(contains? usernames (:name %)))
                      (map (fn [m] [(:name m) (:id m)]))
                      (into {}))]
    (->> slack-username->message
         vec
         (map (fn [[name message]]
                [name (send-message-to-user client (name->id name) message)]))
         (into {}))))

(defrecord SlackClient [client]
  entity/Messenger
  (-send-messages-to-users [_ user-id->message]
    (send-messages-to-users client user-id->message)))

(defn make-slack-client
  [token]
  (->SlackClient (get-slack-client token)))