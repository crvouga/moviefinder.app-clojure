(ns moviefinder-app.media-feedback.media-feedback
  (:require [moviefinder-app.handle :as handle]
            [moviefinder-app.media-feedback.media-feedback-type :as media-feedback-type]
            [moviefinder-app.route :as route]))


(defn route-clicked-feedback [media media-feedback]
  (-> 
   (merge {:route/name :route/clicked-feedback} media-feedback (select-keys media [:media/id :media/type])) 
   route/encode))


(defn icon-button [props]
  [:button
   (-> props
       (dissoc :icon-button/icon :icon-button/label)
       (merge {:class (str "flex-1 flex flex-col items-center justify-center text-xs w-16 h-16 aspect-square font-bold rounded-full drop-shadow-lg")
               :style {:text-shadow "0px 0px 3px rgba(0, 0, 0, 1)"}}))
   [:span.drop-shadow-lg {:class (when (props :icon-button/selected?) "text-blue-500 stroke-white stroke-1")}
    (-> props :icon-button/icon)]
   (-> props :icon-button/label)])


(defn has-feedback? [media media-feedback]
  (->> media
       :media/media-feedback
       (map :media-feedback/type)
       set
       (some #{(media-feedback :media-feedback/type)})))

(defn view-media-feedback-form-button [media media-feedback]
  (icon-button
   {:icon-button/label (media-feedback-type/label media-feedback)
    :icon-button/selected? (has-feedback? media media-feedback)
    :icon-button/icon (media-feedback-type/icon media-feedback)
    :hx-post (route-clicked-feedback media media-feedback) 
    :hx-swap "outerHTML"
    :hx-target "#media-feedback-form-buttons"}))

(def media-feedback-keys [:media-feedback/type
                          :media-feedback/user-id 
                          :media-feedback/media-id 
                          :media-feedback/media-type])

(defn ->media-feedback [input]
  (-> input
      (select-keys media-feedback-keys)))


(def media-feedback-type-order [:media-feedback-type/seen
                                :media-feedback-type/not-seen
                                :media-feedback-type/like
                                :media-feedback-type/dislike
                                :media-feedback-type/interested
                                :media-feedback-type/not-interested])

(defn view-media-feedback-form-buttons [media]
  [:div#media-feedback-form-buttons.w-full.flex.flex-col.justify-center.items-center.pointer-events-auto.gap-1
   (for [feedback-type media-feedback-type-order]
     (view-media-feedback-form-button media (->media-feedback {:media-feedback/type feedback-type})))])

(defn view-media-feedback-form [media]
  [:div.absolute.bottom-2.right-2.pointer-events-none
   (view-media-feedback-form-buttons media)])


(defn put-feedback! [input]
  input)


#_(defn assoc-media-feedback [input]
  (-> input
      (assoc (->media-feedback (get input :media-feedback/type)))))

(defmethod handle/hx-post :route/clicked-feedback [request]
  (-> request
      (merge (-> request :request/route))
      put-feedback!
      (handle/html view-media-feedback-form-buttons)))