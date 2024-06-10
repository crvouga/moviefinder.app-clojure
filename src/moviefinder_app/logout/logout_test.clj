(ns moviefinder-app.logout.logout-test
  (:require [clojure.test :refer [deftest is testing]]
            [moviefinder-app.login.send-login-link :as send-login-link]
            [moviefinder-app.login.use-login-link :as use-login-link]
            [moviefinder-app.logout.logout :as logout]
            [moviefinder-app.deps :as deps]
            [moviefinder-app.user.user :as user]
            [moviefinder-app.user-session.user-session-db :as user-session-db]))

(defn fixture []
  (let [deps (deps/deps-test)
        user (user/random!)
        session {:session/id (java.util.UUID/randomUUID)}
        login-link (send-login-link/send-login-link! (merge deps user))
        _ (use-login-link/use-login-link! (merge deps login-link session))]
    (merge deps user)))

(comment
  (def f (fixture))
  f
  (def before (first (user-session-db/find-by-user-id!
                      (f :user-session-db/user-session-db)
                      (f :user/id))))
  before
  )

(deftest logout-test
  (testing "logout"
    (let [f (fixture)
          before (first (user-session-db/find-by-user-id!
                         (f :user-session-db/user-session-db)
                         (f :user/id)))
          _ (logout/logout! f)
          after (first (user-session-db/find-by-user-id!
                        (f :user-session-db/user-session-db)
                        (f :user/id)))]
      (is (not (nil? before)))
      (is (nil? after)))))