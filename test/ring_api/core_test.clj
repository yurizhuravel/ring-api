(ns ring-api.core-test
  (:require [clojure.test :refer :all]
            [ring-api.core :as core]
            [clj-http.client :as http]))

(use-fixtures :once
  (fn [tests]
    (core/start-server)
    (tests)
    (core/stop-server)))

(def path (str "http://localhost:" core/http-port "/"))

(deftest root
  (testing "root route"
    (let [result (http/get (str path "/"))]
    (is (= (:status result) 200))
    (is (= (:body result) "Welcome to Root")))))

(deftest test-data
  (testing "test-data route"
    (let [result (http/get (str path "/test-data"))]
    (is (= (:status result) 200))
    (is (= (:body result) "Welcome to the test data route")))))

(deftest bad-route-data
  (testing "test-data route"
    (let [result (http/get (str path "/BAD-ROUTE") {:throw-exceptions false})]
    (is (= (:status result) 404))
    (is (= (:body result) "Route Not Found")))))

(deftest counter-route
         (testing "counter route"
                  (let [result (http/get (str path "/counter"))
                        result-second (http/get (str path "/counter"))]
                       (is (= (:status result) 200))
                       (is (= (:body result-second) (str (+ (read-string (:body result)) 1)))))))