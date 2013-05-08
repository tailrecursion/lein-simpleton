(ns leiningen.simpleton
  (:require [clojure.java.io :as io])
  (:import [java.io File]
          [com.sun.net.httpserver HttpHandler HttpExchange HttpServer]
          [java.net InetSocketAddress HttpURLConnection]
          [java.io IOException FilterOutputStream
                   BufferedInputStream FileInputStream]
          [java.net URLDecoder]))

(def message "If it was so, it might be; and if it were so, it would be; but as it isn't, it ain't.")
(def mailbox (promise))

(def mime-types
  {"jpg"  "image/jpeg"
   "gif"  "image/gif"
   "png"  "image/png"
   "html" "text/html"
   "htm"  "text/html"
   "css"  "text/css"})

(.addShutdownHook
 (Runtime/getRuntime)
 (Thread. (fn []
            (deliver mailbox "Shutting down Simpleton..."))))

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
        (.add (.getResponseHeaders exchange)
              "Content-Type" "application/edn")
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
                 f "</a><br>"))
          ["</body></html>"])))

(defn listing [file]
  (-> file .list sort))

(defn get-extension [filename]
  (.substring filename (+ 1 (.lastIndexOf filename "."))))

(defn read-bytes [file]
  (let [out    (byte-array (.length file))
        stream (io/input-stream file)]
    (.read stream out 0 (alength out))
    out))

(defn serve [exchange file]
  (let [out (read-bytes file)
        ext (get-extension (.getName file))]
    (.add (.getResponseHeaders exchange)
          "Content-Type" (get mime-types ext "text/plain"))
    (.sendResponseHeaders exchange HttpURLConnection/HTTP_OK (alength out))
    (with-open [resp (.getResponseBody exchange)]
      (.write resp out 0 (alength out)))))

(defn fs-handler []
  (proxy [HttpHandler] []
    (handle [exchange]
      (let [uri (URLDecoder/decode (str (.getRequestURI exchange)))
            f (File. (str "." uri))
            filenames (listing f)]
        (if (.isDirectory f)
          (do (.add (.getResponseHeaders exchange)
                    "Content-Type"
                    (get mime-types "html"))
              (if-let [idx (some #{"index.html" "index.htm"} filenames)]
                (serve exchange (File. (str "." uri "/" idx)))
                (respond exchange (html uri filenames))))
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

(defn ^:no-project-needed simpleton
  "Starts a simple webserver with the local directory as its root."
  [project & [port :as args]]
  (try
    (let [port (Integer/parseInt port)
          type (second args)]
      (println (str "Starting " (if type type "file") " server on port " port))
      (case type
        "hello" (new-server port "/" (default-handler message))
        "echo" (new-server port "/" (echo-handler))
        (new-server port "/" (fs-handler))))
    (println)
    (println @mailbox)
    (catch NumberFormatException nfe
      (println "Malformed port" port)
      (println "Usage: lein simpleton <port> [server-type]"))))

