## 0. Rationale

Bot powinien:
1. Zaciągać przed planningiem aktualny stan logowania na Jirze.
2. Następnie sprawdzać (zgodnie z ilością dni pracujących od ostatniego planningu) ile powinno godzin być zalogowanych.
3. Jeśli zalogowane godziny nie mieszczą się w marginesie błędu (powiedzmy ~15%) to, dana osoba otrzymuje personalne powiadomienie, żeby sprawdzić JIRĘ i dologować czas.

Założenia ludzkie:
1. “przecież można byłoby robić @channel” – nie, ponieważ ludzie mają tendencję do ignorowania wiadomości, które ich nie dotyczą, średnio po 3-5 wystąpieniach występuje nasycenie i przestajemy mieć reakcję. Rozwiązaniem są wiadomości bezpośrednie, prywatne, when needed.

Pomysły od czapy:
1. Kogo pilnować bardziej. Jeśli ktoś nagminnie niedologowuje czasu, margines powinien się zmniejszać, potem wracać. Może być fajnym osobnym miniprojektem na jakąś nieliniową logikę, może nawet AI
2. Możliwość reakcji (Łapką w górę/Łapką w dół), żeby badać kiedy bot nam się wykłada na byciu nadgorliwym.

## 1. Entity

### a. Bot configuration/state

- Planning weekday, e.g. thursday
- Planning interval, e.g. two weeks
- Next planning date, 04.05.20

Next planning date is calculated by the bot based on current value of planning weekday and interval.
Next date must be no sooner than next week. We cannot assume that next planning will be calculated
on the same day as the next planning (because plannings can be rescheduled manually by the user).

#### Example #1:

Next planning is scheduled on Thursday (day-of-the-week is 4, interval is 7).
However, on Monday user reschedules planning to Tuesday.
In this case the planning should take place on Tuesday and Thursday next week.

#### Example #2:

Planning is on Tuesday but user reschedules to Thursday. In such case,
planning should take place on Thursday and next week on Tuesday.

If user reconfigures interval or day-of-the-week value, the changes takes effect 
on next scheduling (next planning stays the same).

## 2. Use cases

### a. Reminder job

First and foremost responsibility of the bot is to remind users to log their time.
In order to do this, bot must be able to:

- Schedule its own activity (always one day before planning)
- When activated, load configured users (TBD)
- For each user, determine how many hours were logged
- For each user, decide whether reminder should be sent and send it
- Schedule its next reminder job (determine when next planning should be)

### b. Configuration API

Bot should allow external entities (users) to configure itself.
It should provide REST API to change next planning date, day-of-week
and interval. When internal state changes, appropriate changes
should be made to scheduled jobs.
