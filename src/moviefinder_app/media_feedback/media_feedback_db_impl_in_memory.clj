(ns moviefinder-app.media-feedback.media-feedback-db-impl-in-memory
  (:require [moviefinder-app.media-feedback.media-feedback-db :as media-feedback-db]))



(defrecord InMemoryMediaFeedbackDb [feedbacks!]
  media-feedback-db/MediaFeedbackDb
  (put! [_this _feedbacks])
  (find! [_this _query]))


(defmethod media-feedback-db/->MediaFeedbackDb :media-feedback-db/impl-in-memory
  [_config]
  (->InMemoryMediaFeedbackDb (atom {})))