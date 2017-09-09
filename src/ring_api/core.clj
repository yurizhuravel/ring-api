(ns ring-api.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]))

(def http-port 3000)

(defn make-response [status body]
  { :headers {"Content-Type" "text/html"}
    :status status
    :body body })

(defn root-handler [request]
  (make-response 200 "Welcome to Root"))

(defn test-data-handler [request]
  (make-response 200 "Welcome to the test data route"))

(defn handler [request]
  (case (:uri request)
    "/" (root-handler request)
    "/test-data" (test-data-handler request)))

(defonce running-server (atom nil))

(defn stop-server [] (reset! running-server (.stop @running-server)))

(defn start-server [] (if @running-server
  nil
  (reset! running-server (jetty/run-jetty handler {:port http-port :join? false}))))

(defn -main
  "Start API server."
  [& args]
  (start-server))
