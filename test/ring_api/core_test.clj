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
