(ns moviefinder-app.email.email
  (:require [hiccup2.core]))


(defn new [to subject view-body]
  {:email/to to
   :email/subject subject
   :email/body-html (-> view-body hiccup2.core/html str)})


(defn- view-body-document [view-body]
  [:html {:lang "en" :doctype :html5}
   [:head
    [:title "moviefinder.app"]
    [:meta {:name :description :content "Find movies to watch"}]
    [:meta {:charset "utf-8"}]
    [:link {:rel "icon" :href "data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 36 36'><text y='32' font-size='32'>🍿</text></svg>"}]
    [:meta {:name :viewport :content "width=device-width, initial-scale=1.0"}]
    [:script {:src "https://cdn.tailwindcss.com"}]]
   [:body.bg-neutral-950.text-white
    view-body]])

(defn- append-doc-type [html]
  (str "<!DOCTYPE html>" html))

(defn- ->body-html [email]
  (-> email 
      :email/body-view 
      view-body-document
      hiccup2.core/html 
      str
      append-doc-type))

(defn assoc-body-html [email]
  (assoc email :email/body-html (->body-html email)))