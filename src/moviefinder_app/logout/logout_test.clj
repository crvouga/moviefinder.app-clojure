(ns moviefinder-app.logout.logout-test
  (:require [clojure.test :refer [deftest is testing]]
            [moviefinder-app.login.login-with-email.send-login-link :as send-login-link]
            [moviefinder-app.login.login-with-email.use-login-link :as use-login-link]
            [moviefinder-app.logout.logout :as logout]
            [moviefinder-app.deps :as deps]
            [moviefinder-app.user.user :as user]
            [moviefinder-app.user-session.user-session-db :as user-session-db]
            [moviefinder-app.session :as session]
            [moviefinder-app.user.user-db :as user-db]))

(defn fixture []
  (let [deps (deps/deps-test-int)
        user (user/random!)
        _ (user-db/put! (deps :user-db/user-db) #{user})
        session {:session/id (session/random-session-id!)}
        login-link (send-login-link/send-login-link! (merge deps user))
        _ (use-login-link/use-login-link! (merge deps login-link session))]
    (merge deps user session login-link)))

(comment
  (def f (fixture))
  f
  (select-keys f [:user/id :user/email :session/id])
  (def before (first (user-session-db/find-by-user-id!
                      (f :user-session-db/user-session-db)
                      (f :user/id))))
  before
  (logout/logout! f)
  (def after (first (user-session-db/find-by-user-id!
                      (f :user-session-db/user-session-db)
                      (f :user/id))))
  after
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