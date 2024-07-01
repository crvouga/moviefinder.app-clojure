(ns moviefinder-app.media-feedback.media-feedback-db-impl-in-memory
  (:require [moviefinder-app.media-feedback.media-feedback-db :as media-feedback-db]))



(defrecord InMemoryMediaFeedbackDb [feedbacks!]
  media-feedback-db/MediaFeedbackDb
  (put! [this feedbacks])
  (find! [this query]))