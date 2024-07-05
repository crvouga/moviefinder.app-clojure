(ns moviefinder-app.media.media-db-impl-combine 
  (:require [moviefinder-app.media.media-db :as media-db]
            [moviefinder-app.paginated :as paginated]
            [moviefinder-app.media.media-db-impl-tmdb-movie :as media-db-impl-tmdb-movie]
            [moviefinder-app.media.media-db-impl-tmdb-tv :as media-db-impl-tmdb-tv]
            [moviefinder-app.media.media-db-impl-in-memory :as media-db-impl-in-memory]))

(defrecord MediaDbCombine [media-db media-dbs]
  moviefinder-app.media.media-db/MediaDb
  (get! [_this media-id]
    (or (media-db/get! media-db media-id)
        (some->> media-dbs
                 (map #(media-db/get! % media-id))
                 (some identity))))
  (find! [_this query]
    (->> media-dbs
         (pmap #(media-db/find! % query))
         (apply paginated/combine)
         (paginated/map-results #(sort-by :media/popularity > %))))

  (put-many! [_this media-list]
    (doseq [media-db media-dbs]
      (media-db/put-many! media-db media-list))))


(defn media-db-combine [media-db media-dbs]
  (->MediaDbCombine  media-db media-dbs))


(def media-db-movie (media-db-impl-tmdb-movie/media-db-tmdb-movie))

(def media-db-tv (media-db-impl-tmdb-tv/media-db-tmdb-tv))

(def media-db-in-memory (media-db-impl-in-memory/media-db-in-memory))

(def media-db-combined (media-db-combine media-db-in-memory [media-db-movie media-db-tv]))

(def media-select-keys
  #(select-keys % [:media/title :media/media-type]))

(comment
  (->> (media-db/find! media-db-tv {})
       :paginated/results
       (map media-select-keys)
       (take 10))


  (->> (media-db/find! media-db-movie {})
       :paginated/results
       #_(map media-select-keys)
       (take 10))

  (->> (media-db/find! media-db-combined {})
       :paginated/results
       #_(map media-select-keys)
       (map #(dissoc % :media/videos))
       (map keys)
       (take 100))


  (->> (media-db/find! media-db-movie {})
       #_:paginated/results))
  