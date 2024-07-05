(ns moviefinder-app.media.media)


(defn conj-media-feedback [media media-feedback]
  (-> media
      (update-in [:media-feedbacks] conj media-feedback)))


(defn random! []
  {:media/vote-count (rand-int 1000),
   :media/overview "lorum ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
   :media/title "lorum ipsum dolor sit amet",
   :media/tmdb-id 1001311,
   :media/id 1001311,
   :media/vote-average 6.026,
   :media/release-date "2024-06-05",
   :media/media-type :media-type/movie,
   :media/popularity (rand-int 1000)})


(random!)