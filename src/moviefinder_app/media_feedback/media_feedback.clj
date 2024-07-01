(ns moviefinder-app.media-feedback.media-feedback
  (:require [moviefinder-app.view :as view]
            [moviefinder-app.view.icon :as icon]
            [clojure.set :as set]))

(def not-empty? (comp not empty?))

(defn intersect? [set1 set2]
  (-> (set/intersection set1 set2) not-empty?))

(def icon-props {:class "size-6 shrink-0"})

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


(defn view-seen-toggle-buttons [input]
  (let [seen? (-> input :feedback/type (contains? :feedback-type/seen))
        not-seen? (-> input :feedback/type (contains? :feedback-type/not-seen))
        neither? (not (or seen? not-seen?))]
    (view/toggle-button-group
     {:toggle-button-group/label "Seen?"}
     (view/toggle-button {:toggle-button/icon (icon/eye icon-props)
                          :toggle-button/label (when (or neither? seen?) "Seen")
                          :toggle-button/selected? seen?})
     (view/toggle-button {:toggle-button/icon (icon/eye-slash icon-props)
                          :toggle-button/label (when (or neither? not-seen?) "Not seen")
                          :toggle-button/selected? not-seen?}))))

(defn view-like-toggle-buttons [input]
  (let [like? (-> input :feedback/type (contains? :feedback-type/like))
        dislike? (-> input :feedback/type (contains? :feedback-type/dislike))
        neither? (not (or like? dislike?))]
    (view/toggle-button-group
     {:toggle-button-group/label "Like?"}
     (view/toggle-button {:toggle-button/icon (icon/hand-thumbs-up icon-props)
                          :toggle-button/label (when (or neither? like?) "Like")
                          :toggle-button/selected? like?})
     (view/toggle-button {:toggle-button/icon (icon/hand-thumbs-down icon-props)
                          :toggle-button/label (when (or neither? dislike?) "Dislike")
                          :toggle-button/selected? dislike?}))))
  


(defn view-interested-toggle-buttons [input]
  (let [interested? (-> input :feedback/type (contains? :feedback-type/insterested))
        not-interested? (-> input :feedback/type (contains? :feedback-type/not-interested))
        neither? (not (or interested? not-interested?))]
    (view/toggle-button-group
     {:toggle-button-group/label "Interested?"}
     (view/toggle-button {:toggle-button/icon (icon/checkmark icon-props)
                          :toggle-button/label (when (or neither? interested?) "Interested")
                          :toggle-button/selected? interested?})
     (view/toggle-button {:toggle-button/icon (icon/x icon-props)
                          :toggle-button/label (when (or neither? not-interested?) "Uninterested")
                          :toggle-button/selected? not-interested?}))))


(def feedbacks [#{:feedback-type/seen}
                #{:feedback-type/not-seen}
                #{:feedback-type/seen :feedback-type/like}
                #{:feedback-type/seen :feedback-type/dislike}
                #{:feedback-type/not-seen :feedback-type/interested}
                #{:feedback-type/not-seen :feedback-type/not-interested}])

(def feedbacks-cycle (atom (cycle feedbacks)))

(defn view-toggle-button [input]
  [:button.flex-1.p-2.flex.flex-col.items-center.justify-center.gap-1.text-xs
   {}
   (-> input :toggle-button/icon)
   (-> input :toggle-button/label)])

(defn view-toggle-buttons []
  [:div.w-full.flex.border.border-neutral-700.divide-x.divide-neutral-700.rounded-lg.overflow-hidden.bg-neutral-950
   #_[:div.flex-1
    (view/button {:button/label "Seen"
                  :button/w-full? true
                  :button/start (icon/eye icon-props)})] 
   #_[:div.flex-1
    (view/button {:button/label "Not seen"
                  :button/w-full? true
                  :button/start (icon/eye-slash icon-props)})]
   
   (view-toggle-button {:toggle-button/icon (icon/eye icon-props)
                        :toggle-button/label "Seen"})
   (view-toggle-button {:toggle-button/icon (icon/eye-slash icon-props)
                        :toggle-button/label "Not seen"})])

(defn view-media-feedback-form []
  (let [feedback {:feedback/type (first @feedbacks-cycle)}
        ;; seen? (-> feedback :feedback/type (contains? :feedback-type/seen))
        ;; not-seen? (-> feedback :feedback/type (contains? :feedback-type/not-seen))
        ]
    (swap! feedbacks-cycle rest)
    (view-toggle-buttons)
    
    #_[:div.w-full.flex.items-center.justify-center.gap-4.border-t.border-neutral-700
     (view-toggle-buttons)
     #_(view-seen-toggle-buttons feedback)
     #_(when seen?
         (view-like-toggle-buttons feedback))
     #_(when not-seen?
         (view-interested-toggle-buttons feedback))]))