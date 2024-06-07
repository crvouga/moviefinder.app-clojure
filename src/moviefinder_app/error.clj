(ns moviefinder-app.error
  (:require [clojure.test :refer [is]]))

(defn err [error-type error-payload]
  (ex-info "" (merge error-payload {:err/err error-type})))

(defmulti err->msg (comp :err/err ex-data))

(defn ex->err [ex]
  (ex-data ex))

(defn ex->err-type [ex]
  (-> ex ex->err :err/err))

(defmacro thrown-err? [expected-error-type & body]
  `(try
     ~@body
     (is false (str "Expected exception with error type: " ~expected-error-type ", but no exception was thrown."))
     false
     (catch Exception e#
       (let [actual-error-type# (ex->err e#)]
         (is (= ~expected-error-type actual-error-type#)
             (str "Expected exception with error type: " ~expected-error-type ", but got: " actual-error-type#))
         (= ~expected-error-type actual-error-type#)))))