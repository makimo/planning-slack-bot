(ns slack-planning-bot.core.spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::message string?)
(s/def ::int int?)
(s/def ::keyword keyword?)
