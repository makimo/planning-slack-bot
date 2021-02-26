(ns slack-planning-bot.core.action)

(defn make-action
  "Creates new action."
  ([type payload]
   {:type    type
    :error   nil
    :payload payload})
  ([type]
   {:type    type
    :error    nil}))

(defn make-error
  "Creates new error action."
  ([type error payload]
   {:type    type
    :error   error
    :payload payload})
  ([type error]
   {:type    type
    :error   error}))