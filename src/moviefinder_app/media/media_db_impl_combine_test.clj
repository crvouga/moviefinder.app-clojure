(ns moviefinder-app.media.media-db-impl-combine-test
  (:require [moviefinder-app.media.media-db :as media-db]
            [moviefinder-app.media.media-db-impl-tmdb-movie :as media-db-impl-tmdb-movie]
            [moviefinder-app.media.media-db-impl-tmdb-tv :as media-db-impl-tmdb-tv]
            [moviefinder-app.media.media-db-impl-in-memory :as media-db-impl-in-memory]
            [moviefinder-app.media.media-db-impl-combine :as media-db-impl-combine]))

(def media-db-movie (media-db-impl-tmdb-movie/media-db-tmdb-movie))

(def media-db-tv (media-db-impl-tmdb-tv/media-db-tmdb-tv))

(def media-db-in-memory (media-db-impl-in-memory/media-db-in-memory))

(def media-db-combined (media-db-impl-combine/media-db-combine
                        media-db-in-memory
                        [media-db-movie media-db-tv]))

(comment
  (->> (media-db/find! media-db-tv {})
       :paginated/results
       (map #(select-keys % [:media/title :media/media-type]))
       (take 10))


  (->> (media-db/find! media-db-movie {})
       :paginated/results
       (map #(select-keys % [:media/title :media/media-type]))
       (take 10))

  (->> (media-db/find! media-db-combined {})
       :paginated/results
       (map #(select-keys % [:media/media-type :media/title]))
       #_(map #(dissoc % :media/videos))
       (map vals)
       (take 100))
  
  
  (->> (media-db/find! media-db-movie {})
       #_:paginated/results)
)
  