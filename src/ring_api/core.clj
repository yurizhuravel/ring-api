(ns ring-api.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
    [clojure.java.jdbc :as jdbc]
    ))

(def http-port 3000)
(def https-port 3001)

(def db-spec {:password "password"
              :classname "org.postgresql.Driver",
              :subprotocol "postgresql",
              :subname "//localhost:5432/ring-api-test"})

(defn make-response [status body]
  { :headers {"Content-Type" "text/html"}
    :status status
    :body body })

(defn root-handler [request]
  (make-response 200 "Welcome to Root"))

(defn test-data-handler [request]
  (make-response 200 "Welcome to the test data route"))

(defn increment-counter []
      (let [old-count (:count (jdbc/get-by-id db-spec :counter 1))]
      (jdbc/update! db-spec :counter {:count (inc old-count)} ["id = ?" 1])
      old-count))

(defn counter-handler [request]
      (let [counter (increment-counter)]
      (make-response 200 (str counter))))

(defn handler [request]
  (case (:uri request)
    "/" (root-handler request)
    "/test-data" (test-data-handler request)
        "/counter" (counter-handler request)
    (make-response 404 "Route Not Found")))

(defonce running-server (atom nil))

(defn stop-server [] (reset! running-server (.stop @running-server)))

(defn start-server [] (if @running-server
  nil
  (reset! running-server (jetty/run-jetty handler
    { :port http-port
      ; :ssl? true
      ; :ssl-port https-port
      ; :keystore "keystore.jks"
      ; :key-password "simpsons"
      :join? false}))))

(defn -main
  "Start API server."
  [& args]
  ; (create-db-if-none-exists)
  (start-server))
