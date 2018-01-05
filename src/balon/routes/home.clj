(ns balon.routes.home
  (:require [compojure.core :refer :all]
            [balon.views.layout :as layout]
            [clojure.string :as str]
            [hiccup.form :refer :all]
            [hiccup.core :refer [h]]
            [ring.util.response :as ring]
            [balon.models.db :as db]
            [hiccup.util :as util]
            [hiccup.def :refer [defelem]])
  )

(defn format-time [timestamp]
  (-> "dd/MM/yyyy"
      (java.text.SimpleDateFormat.)
      (.format timestamp)))

(defn indexpage []
  (layout/common

    [:header [:p]  ]

    [:body
     [:div {:id "naslov"}
      [:h1 "Welcome to Avala Sport"]
      [:br]]
     [:div {:id "nav"}
      [:ul [:li [:a {:href "/add"} "Dodaj novi"]
            ]
       [:li [:a {:href "/show"} "Prikazi raspored"]]]]]
    [:footer [:p]  ]
    ))
(defn show-termin []

  [:table
   [:thead
    [:tr
     [:th "Id"]
     [:th "Vreme"]
     [:th "Tip"]
     [:th "Aktivnost"]
     [:th "Broj igraca"]
     [:th "Kontakt"]
     [:th "Dan"]
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
            [:td (:kontakt termin)]
            [:td (:dan termin)]
            [:td (format-time(:datum_rezervacije termin))]
            [:td [:a {:href (str "/delete/" (h (:id termin)))} "delete"]]
            [:td [:a {:href (str "/update/" (h (:id termin)))} "update"]]]))])

(defn insert_update [& [vreme tip aktivnost broj_igraca kontakt dan error id]]
  (layout/common

    [:header [:p]  ]
    [:h1 (if (nil? id) "Dodaj novi termin" "Azuriraj termin")]
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
             [:p "Kontakt osoba:"]
             (text-field "kontakt" kontakt)
             [:p "Dan:"]
             (drop-down "dan" ["ponedeljak" "utorak" "sreda" "cetvrtak" "petak" "subota" "nedelja" ] dan)
             [:br] [:br]
             (submit-button
                            (if (nil? id) "Unesi" "Azuriraj"))
             [:hr]
             [:p {:style "color:red;"} error])
    [:div {:id "naz"} [:ul [:li [:a {:href "/" :class "back"} "Home"]]]]))

(defn parse-number [s]
  (if (re-find #"^-?\d+\.?\d*$" s)
    (read-string s)))



(defn save-termin [vreme tip aktivnost broj_igraca kontakt dan & [id]]
  (cond
    (empty? vreme)
    (insert_update vreme tip aktivnost broj_igraca kontakt dan " unesi vreme" id)
    (empty? tip)
    (insert_update vreme tip aktivnost broj_igraca kontakt dan" unesi tip" id)

    (empty? aktivnost)
    (insert_update vreme tip aktivnost broj_igraca kontakt dan" unesi aktivnost "id)
    (nil? (parse-number broj_igraca))
    (insert_update vreme tip aktivnost broj_igraca kontakt dan  " unesi broj igraca" id)
    (<= (parse-number broj_igraca) 1)
    (insert_update  vreme tip aktivnost broj_igraca kontakt dan "Broj igraca mora biti veci od 1" id)
    (empty? kontakt)
    (insert_update vreme tip aktivnost broj_igraca kontakt  dan " Uneti kontakt osobu" id)
    (empty? dan)
    (insert_update vreme tip aktivnost broj_igraca kontakt dan " Uneti dan" id)
    :else
    (do
      (if (nil? id)
        (db/save-termin vreme tip aktivnost broj_igraca kontakt dan)
        (db/update-termin id vreme tip aktivnost broj_igraca kontakt dan))
      (ring/redirect "/show"))))




(defn delete-termin [id]
  (when-not (str/blank? id)
    (db/delete-termin id))
  (ring/redirect "/show"))

(defn show-termini [termin]
  (insert_update (:vreme termin) (:tip termin) (:aktivnost termin) (:broj_igraca termin) (:kontakt termin) (:dan termin)  nil (:id termin)))
(defn show []
  (layout/common
    [:header [:p]  ]
    [:h1 "Termini"]
    [:div {:id "tablica1"}
     (show-termin)]
    [:div {:id "naj"} [:ul [:li [:a {:href "/" :class "back"} "Home"]]]]))

(defroutes home-routes
           (GET "/" [] (indexpage))
           (GET "/add" [] (insert_update))
           (GET "/add" [vreme tip aktivnost broj_igraca kontakt dan error id] (insert_update vreme tip aktivnost broj_igraca kontakt dan error id))
           (GET "/show" [] (show))
           (POST "/save" [vreme tip aktivnost broj_igraca kontakt dan id] (save-termin vreme tip aktivnost broj_igraca kontakt dan id))
           (GET "/delete/:id" [id] (delete-termin id))
           (GET "/update/:id" [id] (show-termini (db/find-termin id))))
