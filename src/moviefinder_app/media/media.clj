(ns moviefinder-app.media.media)


(defn conj-media-feedback [media media-feedback]
  (-> media
      (update-in [:media-feedbacks] conj media-feedback)))