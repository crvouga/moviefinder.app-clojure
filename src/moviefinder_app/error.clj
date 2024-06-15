(ns moviefinder-app.error
  (:require [clojure.test :refer [is]]))

(defn ex [error-type & error-payload]
  (ex-info "" (apply merge (conj error-payload {:err/err error-type}))))


(defmulti err->msg :err/err)

(defn ex->err [ex]
  (ex-data ex))

(defn ex->err-type [ex & _args]
  (-> ex ex->err :err/err))

(defmacro try-catch [body]
  `(try
     [nil ~body]
     (catch Exception e#
       [(ex->err e#) nil])))

(defmacro thrown-err? [expected-error-type & body]
  `(try
     ~@body
     (is false (str "Expected exception with error type: " ~expected-error-type ", but no exception was thrown."))
     false
     (catch Exception e#
       (let [actual-error-type# (ex->err-type e#)]
         (is (= ~expected-error-type actual-error-type#)
             (str "Expected exception with error type: " ~expected-error-type ", but got: " actual-error-type#))
         (= ~expected-error-type actual-error-type#)))))