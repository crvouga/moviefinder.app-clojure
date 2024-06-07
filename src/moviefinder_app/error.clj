(ns moviefinder-app.error
  (:require [clojure.test :refer [is]]))

(defn err [error-type error-payload]
  (ex-info "" (merge error-payload {:err/err error-type})))

(defmulti err->msg (comp :err/err ex-data))

(defn ex->error-type [ex]
  (:err/err (ex-data ex)))

(defmacro thrown-err? [expected-error-type & body]
  `(try
     ~@body
     (is false (str "Expected exception with error type: " ~expected-error-type ", but no exception was thrown."))
     false
     (catch Exception e#
       (let [actual-error-type# (ex->error-type e#)]
         (is (= ~expected-error-type actual-error-type#)
             (str "Expected exception with error type: " ~expected-error-type ", but got: " actual-error-type#))
         (= ~expected-error-type actual-error-type#)))))