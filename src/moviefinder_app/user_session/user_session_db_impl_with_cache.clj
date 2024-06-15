(ns moviefinder-app.user-session.user-session-db-impl-with-cache
  (:require [moviefinder-app.user-session.user-session-db :as user-session-db]))



(defrecord WithCache [input]
  user-session-db/UserSessionDb
  (find-by-session-id!
   [_this session-id]
   (let [cache (-> input :with-cache/cache)
         session (user-session-db/find-by-session-id! cache session-id)]
     session))

  (find-by-user-id!
   [_this user-id]
   (let [cache (-> input :with-cache/cache)
         sessions (user-session-db/find-by-user-id! cache user-id)]
     sessions))

  (zap-by-session-id!
   [_this session-id]
   (let [source (-> input :with-cache/source)
         cache (-> input :with-cache/cache)]
     (user-session-db/zap-by-session-id! source session-id)
     (user-session-db/zap-by-session-id! cache session-id)))

  (put! 
   [_this user-sessions] 
   (let [source (-> input :with-cache/source)
         cache (-> input :with-cache/cache)]
     (user-session-db/put! source user-sessions)
     (user-session-db/put! cache user-sessions))))

(defmethod user-session-db/->UserSessionDb :user-session-db-impl/with-cache [input]
  (->WithCache input))
