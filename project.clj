(defproject slack-planning-bot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/data.json "1.0.0"]
                 [cheshire "5.10.0"]
                 [io.pedestal/pedestal.service "0.5.8"]
                 [io.pedestal/pedestal.jetty "0.5.8"]
                 [clojurewerkz/quartzite "2.1.0"]
                 [com.slack.api/slack-api-client "1.1.1"]
                 [org.slf4j/jul-to-slf4j "1.7.26"]
                 [org.slf4j/jcl-over-slf4j "1.7.26"]
                 [org.clojure/test.check "1.1.0"]
                 [org.slf4j/log4j-over-slf4j "1.7.26"]
                 [environ "1.2.0"]
                 [mount "0.1.11"]
                 [clj-http "3.10.1"]
                 [clj-time "0.15.2"]]

  :plugins [[lein-environ "1.2.0"]]
  :main ^:skip-aot slack-planning-bot.delivery.server
  :target-path "target/%s"
  :profiles {:dev {:env {:url "http://0.0.0.0:8080/"
                         :jira-host "https://makimo.atlassian.net"}
                   :aliases {"run-dev" ["trampoline" "run" "-m" "name-service.delivery.server/run-dev"]}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.8"]]
                   }
             :uberjar {:aot :all}})