(ns moviefinder-app.media-feedback.media-feedback-db)




(defprotocol MediaFeedbackDb
  (put! [this feedbacks])
  (find! [this query]))

(defmulti ->MediaFeedbackDb :media-feedback-db/impl)