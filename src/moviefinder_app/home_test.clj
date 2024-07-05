(ns moviefinder-app.home-test 
  (:require [moviefinder-app.deps :as deps]
            [moviefinder-app.media.media-db :as media-db]))

(def deps (deps/deps-test-unit))

(comment
  (def media-db (:media-db/media-db deps))

  media-db
  
  (media-db/find! media-db {})


  
  )