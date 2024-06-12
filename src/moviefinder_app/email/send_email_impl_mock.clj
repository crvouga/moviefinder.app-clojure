(ns moviefinder-app.email.send-email-impl-mock
  (:require [moviefinder-app.email.email :refer [assoc-body-html]]
            [moviefinder-app.email.send-email]))


(defn- conj-email [state email]
  (let [sent-emails (get state ::sent-emails #{})
        sent-emails-new (conj sent-emails email)
        state-new (assoc state ::sent-emails sent-emails-new)]
    state-new))

(defrecord SendEmailImplMock [state! input]
  moviefinder-app.email.send-email/SendEmail
  (send-email!
    [_this email]
    (let [email-with-html (assoc-body-html email)]
      (swap! state! conj-email email-with-html)
      (when (:send-email/log? input)
        (println ::send-email!)
        (println (pr-str (select-keys email-with-html [:email/to :email/subject :email/body-view]))) )))

  (get-sent-emails!
   [_this]
   (get @state! ::sent-emails #{})))


(defmethod moviefinder-app.email.send-email/->SendEmail :send-email-impl/mock
  [input]
  (->SendEmailImplMock (atom {}) input))