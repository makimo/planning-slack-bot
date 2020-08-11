(defproject slack-planning-bot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [environ "0.5.0"]
                 [com.slack.api/slack-api-client "1.1.1"]
                 [org.julienxx/clj-slack "0.6.3"]]

  :env {:slack-token "xoxb-4089195133-1292908442416-pJ1MP6q4pbvqqNkaOyEjN8lW"}

  :main ^:skip-aot slack-planning-bot.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
