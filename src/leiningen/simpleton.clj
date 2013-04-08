(ns leiningen.simpleton
  (import [java.io File]
          [com.sun.net.httpserver HttpHandler HttpExchange HttpServer]
          [java.net InetSocketAddress HttpURLConnection]
          [java.io IOException FilterOutputStream]))

(.addShutdownHook
 (Runtime/getRuntime)
 (Thread. #(println "Shutting down Simpleton...")))

(defn simpleton
  "I don't do a lot."
  [project & [port :as args]]
  (println "Starting server on port " port)
  (new-server 8080 "/joy/hello" (default-handler "Hello Cleveland"))
  @(promise))

