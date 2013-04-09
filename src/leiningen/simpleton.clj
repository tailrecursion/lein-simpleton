(ns leiningen.simpleton
  (:require [clojure.java.io :as io])
  (:import [java.io File]
          [com.sun.net.httpserver HttpHandler HttpExchange HttpServer]
          [java.net InetSocketAddress HttpURLConnection]
          [java.io IOException FilterOutputStream]))

(def message "If it was so, it might be; and if it were so, it would be; but as it isn't, it ain't.")
(def mailbox (promise))
(def | File/separator)

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

(defn mapify-headers [hmap]
  (into {} (for [[k v] (.entrySet hmap)]
             [k (vec v)])))

(defn echo-handler []
  (proxy [HttpHandler] []
    (handle [exchange]
      (let [headers (mapify-headers (.getRequestHeaders exchange))]
        (respond exchange (prn-str headers))))))

(defn html
  [root things]
  (apply str
         (concat
          ["<html><head></head><body>"]
          (for [f things]
            (str "<a href='"
                 (str root (if (= "/" root) "" File/separator) f)
                 "'>"
                 f
                 "</a><br>"))
          ["</body></html>"])))

(defn listing [file]
  (-> file .list sort))

(defn serve [exchange file]
  (.add (.getResponseHeaders exchange)
        "Content-Type"
        "text/plain")
  (let [out (byte-array (.length file))
        stream (java.io.BufferedInputStream. (java.io.FileInputStream. file))]
    (.sendResponseHeaders exchange HttpURLConnection/HTTP_OK (alength out))
    (.read stream out 0 (alength out))
    (.write (.getResponseBody exchange)
            out 0 (alength out))))

(defn fs-handler []
  (proxy [HttpHandler] []
    (handle [exchange]
      (let [uri (str (.getRequestURI exchange))
            f (File. (str "." uri))
            filenames (listing f)]
        (if (.isDirectory f)
          (do (.add (.getResponseHeaders exchange)
                    "Content-Type"
                    "text/html")
              (respond exchange (html uri filenames)))
          (try
            (serve exchange f)
            (catch Exception e
              (println (.getMessage e)))))))))

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
  ;;(new-server 8080 "/" (echo-handler))
  (new-server 8080 "/" (fs-handler))
  (println @mailbox))

