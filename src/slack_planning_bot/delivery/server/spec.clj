(ns slack-planning-bot.delivery.server.spec
  (:require [clojure.spec.alpha :as s]
            [slack-planning-bot.delivery.server.utils :as utils]))


; Interval must be integer number.
(s/def ::interval (s/and int? #(>= % 1)))

; Day of the week must be integer number from 1 to 5.
; Each number represent day of the week.
; Starts from 1-Monday and ends to 5-Friday.
(s/def ::day_of_week (s/and int? #(>= % 1) #(<= % 5)))

;(s/def ::next_planning utils/valid-date?)
(s/def ::next_planning (s/and string? #(re-matches #"^\d{2}-\d{2}-\d{4}$" %)))

(s/def ::post-next-planning-date-request (s/keys :req-un [::next_planning]))

(s/def ::post-configure-bot-request (s/keys :req-un [(or ::interval ::day_of_week)]))
