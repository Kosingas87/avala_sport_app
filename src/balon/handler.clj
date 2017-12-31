(ns balon.handler
  (:require [compojure.core :refer [defroutes routes]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [hiccup.middleware :refer [wrap-base-url]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [balon.routes.home :refer [home-routes]]
            [balon.models.db :as db]))

(defn init []
  (if-not (.exists (java.io.File. "./models.sq3"))
    (db/create-termin-table)))

(defn destroy []
  (println "Avala sport app is shutting down"))

(defroutes app-routes
           (route/resources "/")
           (route/not-found "Page Not Found"))

(def app
  (-> (routes home-routes app-routes)
      (handler/site)
      (wrap-base-url)))