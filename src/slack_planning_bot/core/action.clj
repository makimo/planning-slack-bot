(ns slack-planning-bot.core.action)

(defn make-action
  "Creates a new action."
  ([type payload]
   {::type    type
    ::error   nil
    ::payload payload})
  ([type]
   {::type    type
    :error    nil}))

(defn make-error
  "Creates a new error action."
  ([type error payload]
   {::type    type
    ::error   error
    ::payload payload})
  ([type error]
   {::type    type
    ::error   error}))