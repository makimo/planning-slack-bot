FROM clojure:lein AS builder

COPY . /app
WORKDIR /app

RUN ["lein", "uberjar"]

FROM openjdk:15-alpine

COPY --from=builder /app/target/slack-planning-bot-*-SNAPSHOT-standalone.jar /app/planning-bot.jar

WORKDIR /app

CMD ["java", "-jar", "/app/planning-bot.jar"]
