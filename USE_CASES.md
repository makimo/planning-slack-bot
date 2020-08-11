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
