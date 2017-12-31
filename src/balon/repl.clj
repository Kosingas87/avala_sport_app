(ns balon.repl
  (:use balon.handler
        ring.server.standalone
        [ring.middleware file-info file]))

(defonce server (atom nil))

(defn get-handler []
  (-> #'app
      (wrap-file "resources")
      (wrap-file-info)))

(defn start-server
  "For starting server from repl environment"
  [& [port]]
  (let [port (if port (Integer/parseInt port) 8080)]
    (reset! server
            (serve (get-handler)
                   {:port port
                    :init init
                    :auto-reload? true
                    :destroy destroy
                    :join true}))
    (println (str "You can reach the site from http://localhost:" port))))

(defn stop-server []
  (.stop @server)
  (reset! server nil))

(start-server "8080")