(ns moviefinder-app.email.email
  (:require [hiccup2.core]))


(defn new [to subject view-body]
  {:email/to to
   :email/subject subject
   :email/body-html (-> view-body hiccup2.core/html str)})