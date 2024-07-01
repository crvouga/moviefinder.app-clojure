(ns moviefinder-app.media-feedback.media-feedback-type
  (:require [moviefinder-app.view.icon :as icon]))

;; 
;; 
;; 

(defmulti label :media-feedback/type)

(defmulti icon :media-feedback/type)

;; 
;; 
;; 

(defmethod label :media-feedback-type/seen [_]
  "Seen")

(defmethod icon :media-feedback-type/seen [input]
  (icon/eye input))

;; 
;; 
;; 

(defmethod label :media-feedback-type/not-seen [_]
  "Not Seen")

(defmethod icon :media-feedback-type/not-seen [input]
  (icon/eye-slash input))

;; 
;; 
;; 

(defmethod label :media-feedback-type/like [_]
  "Liked")

(defmethod icon :media-feedback-type/like [input]
  (icon/hand-thumbs-up input))

;; 
;; 
;; 

(defmethod label :media-feedback-type/dislike [_]
  "Disliked")

(defmethod icon :media-feedback-type/dislike [input]
  (icon/hand-thumbs-down input))

;; 
;; 
;; 

(defmethod label :media-feedback-type/interested [_]
  "Looks Good")

(defmethod icon :media-feedback-type/interested [input]
  (icon/checkmark input))

;; 
;; 
;; 

(defmethod label :media-feedback-type/not-interested [_]
  "Looks Bad")

(defmethod icon :media-feedback-type/not-interested [input]
  (icon/x input))

;; 
;; 
;; 