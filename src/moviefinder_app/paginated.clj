(ns moviefinder-app.paginated)


(defn map-results [paginated map-result]
  (let [results-new (map map-result (:paginated/results paginated))]
    (assoc paginated :paginated/results results-new)))

(defn- interleave-append [seq1 seq2]
  (let [interleaved (interleave seq1 seq2)
        remaining (drop (count interleaved) (concat seq1 seq2))]
    (concat interleaved remaining)))

(defn- combined-results [paginateds]
  (->> paginateds
       (map :paginated/results)
       (reduce interleave-append [])
       shuffle))

(->> [[1 2 3] [4 5] []] 
     (reduce interleave-append []))

(defn- combined-total-results [paginateds]
  (->> paginateds
       (map :paginated/total-results)
       (apply max)))

(defn- combined-total-pages [paginateds]
  (->> paginateds
       (map :paginated/total-pages)
       (apply max)))

(defn- combined-page [paginateds]
  (->> paginateds
       (map :paginated/page)
       (apply min)))

(defn combine
  "Combine paginated results into a single paginated result."
  [& paginateds]
  {:paginated/page (combined-page paginateds)
   :paginated/total-pages (combined-total-pages paginateds)
   :paginated/total-results (combined-total-results paginateds)
   :paginated/results (combined-results paginateds)})

(defn init []
  {:paginated/page 0
   :paginated/total-pages 0
   :paginated/total-results 0
   :paginated/results []})