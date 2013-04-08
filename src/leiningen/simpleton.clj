(ns leiningen.simpleton
  (import [java.io File]
          [com.sun.net.httpserver HttpHandler HttpExchange HttpServer]
          [java.net InetSocketAddress HttpURLConnection]
          [java.io IOException FilterOutputStream]))

(def message "If it was so, it might be; and if it were so, it would be; but as it isn't, it ain't.")
(def mailbox (promise))

(.addShutdownHook
 (Runtime/getRuntime)
 (Thread. (fn []
            (println)
            (println "Shutting down Simpleton...")
            (deliver mailbox "Bye"))))

(defn respond [exchange body]
  (.sendResponseHeaders exchange HttpURLConnection/HTTP_OK 0)
  (doto (.getResponseBody exchange)
    (.write (.getBytes body))
    (.close)))

(defn default-handler
  [txt]
  (proxy [HttpHandler] []
    (handle [exchange]
      (respond exchange txt))))

(defn echo-handler []
  (proxy [HttpHandler] []
    (handle [exchange]
      (let [headers (.getRequestHeaders exchange)
            entry-map (into {} (for [[k v] (.entrySet headers)]
                                 [k (vec v)]))]
        (respond exchange (prn-str entry-map))))))

(defn html
  [things]
  (apply str
         (concat
          ["<html><head></head><body>"]
          (for [f things]
            (str "<a href='"
                 f
                 "'>"
                 f
                 "</a><br>"))
          ["</body></html>"])))

(defn listing [file]
  (-> file .list seq sort))

(defn fs-handler []
  (proxy [HttpHandler] []
    (handle [exchange]
      (let [f (File. ".")]
        (.add (.getResponseHeaders exchange)
              "Content-Type"
              "text/html")
        (respond exchange (html (listing f)))))))

(defn new-server
  [port path handler]
  (doto (HttpServer/create (InetSocketAddress. port) 0)
    (.createContext path handler)
    (.setExecutor nil)
    (.start)))

(defn simpleton
  "I don't do a lot."
  [project & [port :as args]]
  (println "Starting server on port" port)
  ;;  (new-server 8080 "/" (default-handler message))
  ;; (new-server 8080 "/" (echo-handler))
  (new-server 8080 "/" (fs-handler))
  (println @mailbox))

