(ns moviefinder-app.error)


(defn error [error-type error-payload]
  (ex-info "" (merge error-payload {:error/error error-type})))

(defn throw-error [error-type error-payload]
  (throw (error error-type error-payload)))

(defmulti error->message (comp :error/error ex-data))

(defn ex->error-type [ex]
  (:error/error (ex-data ex)))


(defn error-thrown? [error-type & expr]
  )