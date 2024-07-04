(ns moviefinder-app.media-feedback.media-feedback-db-impl
  (:require [moviefinder-app.media-feedback.media-feedback-db :as media-feedback-db]
            [moviefinder-app.media-feedback.media-feedback-db-impl-in-memory]))


(defn in-memory []
  (media-feedback-db/->MediaFeedbackDb
   {:media-feedback-db/impl :media-feedback-db/impl-in-memory}))

