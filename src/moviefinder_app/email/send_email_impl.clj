(ns moviefinder-app.email.send-email-impl
  (:require [moviefinder-app.email.send-email-impl-mock]
            [moviefinder-app.email.send-email :as send-email]))

(defn mock [& args]
  (send-email/->SendEmail
   (apply merge (conj args {:send-email/impl :send-email-impl/mock}))))

