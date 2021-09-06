(ns slack-planning-bot.delivery.server.service
  (:require [io.pedestal.http :as http]
            [slack-planning-bot.delivery.server.use-case :as uc]))

(def routes #{["/date" :post uc/post-next-planning-date :route-name :post-next-planning-date]
              ["/settings" :post uc/post-configure-bot :route-name :post-configure-bot]
              ["/health" :get (fn [_] {:status 204}) :route-name :health-check]})


; Consumed by name-service.server/create-server
; See http/default-interceptors for additional options you can configure
(def service
  {:env :prod
   ; You can bring your own non-default interceptors. Make
   ; sure you include routing and set it up right for
   ; dev-mode. If you do, many other keys for configuring
   ; default interceptors will be ignored.
   ; ::http/interceptors []
   ::http/routes routes

   ; Uncomment next line to enable CORS support, add
   ; string(s) specifying scheme, host and port for
   ; allowed source(s):
   ;
   ; "http://localhost:8080"
   ;
   ;::http/allowed-origins ["scheme://host:port"]

   ; Tune the Secure Headers
   ; and specifically the Content Security Policy appropriate to your service/application
   ; For more information, see: https://content-security-policy.com/
   ;   See also: https://github.com/pedestal/pedestal/issues/499
   ;::http/secure-headers {:content-security-policy-settings {:object-src "'none'"
   ;                                                          :script-src "'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:"
   ;                                                          :frame-ancestors "'none'"}}

   ; Root for resource interceptor that is available by default.
   ::http/resource-path "/public"

   ; Either :jetty, :immutant or :tomcat (see comments in project.clj)
   ; This can also be your own chain provider/server-fn -- http://pedestal.io/reference/architecture-overview#_chain_provider
   ::http/type :jetty
   ::http/host "0.0.0.0"
   ::http/port 8080
   ; Options to pass to the container (Jetty)
   ::http/container-options
   {:h2c? true
    :h2? false
    ;:keystore "test/hp/keystore.jks"
    ;:key-password "password"
    ;:ssl-port 8443
    :ssl? false
    ; Alternatively, You can specify you're own Jetty HTTPConfiguration
    ; via the `:io.pedestal.http.jetty/http-configuration` container option.
    ;:io.pedestal.http.jetty/http-configuration (org.eclipse.jetty.server.HttpConfiguration.)
    }})
