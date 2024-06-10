(ns moviefinder-app.session)


(defn random-session-id! []
  (str "session:" (java.util.UUID/randomUUID)))