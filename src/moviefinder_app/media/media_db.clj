(ns moviefinder-app.media.media-db)

(defprotocol MediaDb  
  (get! [this id])
  (find! [this query])
  (put-many! [this media-list]))

(def query {:q/limit 10
            :q/offset 0
            :q/order [[:media/popularity :q/desc]]
            :q/where [:q/and
                      [:q/or
                       [:q/= :media/type :media/movie]
                       [:q/= :media/type :media/tv-show]]
                      [:q/= :media/type :media/movie]
                      [:q/= :media/release-year 1999]
                      [:q/in :media/genre #{:genre/horror}]]})
