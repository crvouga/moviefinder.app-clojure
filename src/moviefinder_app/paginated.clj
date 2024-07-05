(ns moviefinder-app.paginated)


(defn map-results
  [^clojure.lang.IFn map-result
   ^clojure.lang.IPersistentMap paginated]
  (-> paginated
      (update :paginated/results #(map map-result %))))

(defn- interleave-append [seq1 seq2]
  (let [interleaved (interleave seq1 seq2)
        remaining (drop (count interleaved) (concat seq1 seq2))]
    (concat interleaved remaining)))

(defn- combined-results [paginateds]
  (->> paginateds
       (map :paginated/results)
       (reduce interleave-append [])
       shuffle))

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


(def paginated-a (merge (init) {:paginated/results [1 2 3 4 5]}))
(def paginated-b (merge (init) {:paginated/results [1 2 3 4 5]}))
(combine paginated-a paginated-b)