(defproject slack-planning-bot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [environ "1.2.0"]
                 [com.slack.api/slack-api-client "1.1.1"]
                 [clojurewerkz/quartzite "2.1.0"]
                 [mount "0.1.11"]
                 [clj-http "3.10.1"]
                 [org.clojure/data.json "1.0.0"]]
  :plugins [[lein-environ "1.2.0"]]
  :main ^:skip-aot slack-planning-bot.delivery.server
  :target-path "target/%s"
  :profiles {:dev {:env {:url "http://0.0.0.0:8080/"}}
             :uberjar {:aot :all}})
