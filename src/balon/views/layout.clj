(ns balon.views.layout
  (:require [hiccup.page :refer :all]))

(defn common [& body]
  (html5
    [:head
     [:title "Welcome to Avala Sport"]
     (include-css "/css/style.css")
     ]
    [:body body]))
