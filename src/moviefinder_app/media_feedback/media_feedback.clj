(ns moviefinder-app.media-feedback.media-feedback
  (:require [moviefinder-app.view :as view]
            [moviefinder-app.view.icon :as icon]
            [clojure.set :as set]))

(def not-empty? (comp not empty?))

(defn intersect? [set1 set2]
  (-> (set/intersection set1 set2) not-empty?))

(def icon-props {:class "size-5"})

(defn- view-no-feedback [props]
  (view/action-button-container
   (view/action-buttton {:action-button/icon (icon/eye-slash icon-props)
                         :action-button/label "Not seen"
                         :action-button/active? (->> props :feedback/type (intersect? #{:feedback/not-seen}))
                         #_:action-button/disabled? #_(->> props :feedback/type (intersect? #{:feedback/seen}))})
   (view/action-buttton {:action-button/icon (icon/eye icon-props)
                         :action-button/label "Seen"
                         :action-button/active? (->> props :feedback/type (intersect? #{:feedback/seen}))
                         #_:action-button/disabled? #_(->> props :feedback/type (intersect? #{:feedback/not-seen}))})))

(defn- view-seen []
  [:div.w-full.flex.flex-col
   (view/action-button-container
    (view/action-buttton {:action-button/icon (icon/hand-thumbs-down icon-props)
                          :action-button/label "Dislike"})
    (view/action-buttton {:action-button/icon (icon/hand-thumbs-up icon-props)
                          :action-button/label "Like"}))
   (view-no-feedback
    {:feedback/type #{:feedback/seen}})])

(defn- view-not-seen []
  [:div.w-full.flex.flex-col
   (view/action-button-container
    (view/action-buttton {:action-button/icon (icon/x icon-props)
                          :action-button/label "Not interested"})
    (view/action-buttton {:action-button/icon (icon/checkmark icon-props)
                          :action-button/label "Interested"}))
   (view-no-feedback {:feedback/type #{:feedback/not-seen}})])

(def _views
  [(view-no-feedback {})
   (view-seen)
   (view-not-seen)])


(defn view-seen-toggle-buttons []
  (view/toggle-button-group
    #_{:toggle-button-group/label "Seen?"}
   (view/toggle-button {:toggle-button/icon (icon/eye icon-props)
                        :toggle-button/label "Seen"})
   (view/toggle-button {:toggle-button/icon (icon/eye-slash icon-props)
                        :toggle-button/label "Not seen"})))

(defn view-like-toggle-buttons []
  (view/toggle-button-group
    #_{:toggle-button-group/label "Liked?"}
   (view/toggle-button {:toggle-button/icon (icon/hand-thumbs-up icon-props)
                        :toggle-button/label "Like"})
   (view/toggle-button {:toggle-button/icon (icon/hand-thumbs-down icon-props)
                        :toggle-button/label "Dislike"})))


(defn view-interested-toggle-buttons []
  (view/toggle-button-group
   #_{:toggle-button-group/label "Interested?"}
   (view/toggle-button {:toggle-button/icon (icon/checkmark icon-props)
                        :toggle-button/label "Interested"})
   (view/toggle-button {:toggle-button/icon (icon/x icon-props)
                        :toggle-button/label "Not interested"})))


(defn view-media-feedback-form []
  [:div.w-full.flex.items-center.justify-center.p-2.gap-4.border-t.border-neutral-700
   (view-seen-toggle-buttons)
   (view-like-toggle-buttons)
   (view-interested-toggle-buttons)])