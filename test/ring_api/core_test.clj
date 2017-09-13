(ns ring-api.core-test
  (:require [clojure.test :refer :all]
            [ring-api.core :as core]
            [clj-http.client :as http]
    [clojure.java.jdbc :as jdbc]))

(use-fixtures :once
  (fn [tests]
    (core/start-server)
    (tests)
    (core/stop-server)))

(def path (str "http://localhost:" core/http-port "/"))

(defn drop-table-if-exists [table-name]
      (try
         (seq? (jdbc/query core/db-spec [(str "select * from " table-name)]))
         (jdbc/query core/db-spec [(str "drop table " table-name)])
           (catch Exception e
             (println (str "Table doesn't exist " table-name)))))

(deftest root-test
  (testing "root route"
    (let [result (http/get (str path "/"))]
    (is (= (:status result) 200))
    (is (= (:body result) "Welcome to Root")))))

(deftest test-data-test
  (testing "test-data route"
    (let [result (http/get (str path "/test-data"))]
    (is (= (:status result) 200))
    (is (= (:body result) "Welcome to the test data route")))))

(deftest bad-route-data-test
  (testing "test-data route"
    (let [result (http/get (str path "/BAD-ROUTE") {:throw-exceptions false})]
    (is (= (:status result) 404))
    (is (= (:body result) "Route Not Found")))))

(deftest counter-route-test
         (testing "counter route"
                  (let [result (http/get (str path "/counter"))
                        result-second (http/get (str path "/counter"))]
                       (println "RESULT" result)
                       (is (= (:status result) 200))
                       (is (= (:body result-second) (str (+ (read-string (:body result)) 1)))))))

(deftest create-table-if-not-exists-test
         (testing "creates a db table if it doesn't exist"
                  (drop-table-if-exists "testtable")
                  (let [_ (core/create-table-if-not-exists "testtable")
                        table? (seq? (jdbc/query core/db-spec ["select * from testtable"]))
                        _ (drop-table-if-exists "testtable")]
                  (is table?))))