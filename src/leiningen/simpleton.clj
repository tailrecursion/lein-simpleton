(ns leiningen.simpleton
  (import [java.io File]
          [com.sun.net.httpserver HttpHandler HttpExchange HttpServer]
          [java.net InetSocketAddress HttpURLConnection]
          [java.io IOException FilterOutputStream]))

(def message "If it was so, it might be; and if it were so, it would be; but as it isn't, it ain't.")

(defn default-handler
  [txt]
  (proxy [HttpHandler] []
    (handle [exchange]
      (.sendResponseHeaders exchange HttpURLConnection/HTTP_OK 0)
      (doto (.getResponseBody exchange)
        (.write (.getBytes txt))
        (.close)))))

(defn new-server
  [port path handler]
  (doto (HttpServer/create (InetSocketAddress. port) 0)
    (.createContext path handler)
    (.setExecutor nil)
    (.start)))

(.addShutdownHook
 (Runtime/getRuntime)
 (Thread. #(println "Shutting down Simpleton...")))

(defn simpleton
  "I don't do a lot."
  [project & [port :as args]]
  (println "Starting server on port " port)
  (new-server 8080 "/" (default-handler message))
  @(promise))

