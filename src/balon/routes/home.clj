(ns balon.routes.home
  (:require [compojure.core :refer :all]
            [balon.views.layout :as layout]
            [clojure.string :as str]
            [hiccup.form :refer :all]
            [hiccup.core :refer [h]]
            [ring.util.response :as ring]
            [balon.models.db :as db])
  )

(defn format-time [timestamp]
  (-> "dd/MM/yyyy"
      (java.text.SimpleDateFormat.)
      (.format timestamp)))

(defn indexpage []
  (layout/common
    [:h2 "Welcome to Avala Sport"]
    [:br]
    [:a {:href "/add"} "Add new"]
    [:br]
    [:a {:href "/show"} "Show termins:"]))
(defn show-termin []
  [:table {:border 1}
   [:thead
    [:tr
     [:th "Id"]
     [:th "Vreme"]
     [:th "Tip"]
     [:th "Aktivnost"]
     [:th "Broj igraca"]
     [:th "Datum rezervacije"]
     [:th "Obrisi"]
     [:th "Azuriraj"]]]

   (into [:tbody]
         (for [termin (db/read-termin)]
           [:tr
            [:td (:id termin)]
            [:td (:vreme termin)]
            [:td (:tip termin)]
            [:td (:aktivnost termin)]
            [:td (:broj_igraca termin)]
            [:td (format-time(:datum_rezervacije termin))]
            [:td [:a {:href (str "/delete/" (h (:id termin)))} "delete"]]
            [:td [:a {:href (str "/update/" (h (:id termin)))} "update"]]]))])

(defn insert_update [& [vreme tip aktivnost broj_igraca error id]]
  (layout/common
    [:h2 (if (nil? id) "Add new termin" "Updating termin")]
    (form-to {:id "frm_insert"}
             [:post "/save"]
             (if (not (nil? id))
               [:p "Id:"])
             (if (not (nil? id))
               (text-field {:readonly true} "id" id))
             [:p "Vreme:"]
             (text-field "vreme" vreme)
             [:p "Tip:"]
             (text-field {:id "tip"} "tip" tip)

             [:p "Aktivnost:"]
             (text-field "aktivnost" aktivnost)
             [:p "Broj igraca:"]
             (text-field {:id broj_igraca} "broj_igraca"  broj_igraca)
             [:br] [:br]
             (submit-button {:onclick " return javascript:validateInsertForm()"}
                            (if (nil? id) "Insert" "Update"))
             [:hr]
             [:p {:style "color:red;"} error])
    [:a {:href "/" :class "back"} "Home"]))

(defn parse-number [s]
  (if (re-find #"^-?\d+\.?\d*$" s)
    (read-string s)))



(defn save-termin [vreme tip aktivnost broj_igraca & [id]]
  (cond
    (empty? vreme)
    (insert_update vreme tip aktivnost broj_igraca " unesi vreme" id)
    (empty? tip)
    (insert_update vreme tip aktivnost broj_igraca " unesi tip" id)

    (empty? aktivnost)
    (insert_update vreme tip aktivnost broj_igraca " unesi aktivnost "id)
    (nil? (parse-number broj_igraca))
    (insert_update vreme tip aktivnost broj_igraca  " unesi broj igraca" id)
    (<= (parse-number broj_igraca) 1)
    (insert_update  vreme tip aktivnost broj_igraca "Broj igraca mora biti veci od 1" id)
    :else
    (do
      (if (nil? id)
        (db/save-termin vreme tip aktivnost broj_igraca)
        (db/update-termin id vreme tip aktivnost broj_igraca))
      (ring/redirect "/show"))))




(defn delete-termin [id]
  (when-not (str/blank? id)
    (db/delete-termin id))
  (ring/redirect "/show"))

(defn show-termini [termin]
  (insert_update (:vreme termin) (:tip termin) (:aktivnost termin) (:broj_igraca termin)  nil (:id termin)))
(defn show []
  (layout/common
    [:h1 "Termini"]
    (show-termin)
    [:a {:href "/" :class "back"} "Home"]))

(defroutes home-routes
           (GET "/" [] (indexpage))
           (GET "/add" [] (insert_update))
           (GET "/add" [vreme tip aktivnost broj_igraca error id] (insert_update vreme tip aktivnost broj_igraca error id))
           (GET "/show" [] (show))
           (POST "/save" [vreme tip aktivnost broj_igraca id] (save-termin vreme tip aktivnost broj_igraca id))
           (GET "/delete/:id" [id] (delete-termin id))
           (GET "/update/:id" [id] (show-termini (db/find-termin id))))
