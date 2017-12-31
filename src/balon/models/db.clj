(ns balon.models.db
  (:require [clojure.java.jdbc :as sql])
  (:import java.sql.DriverManager))

(def db {:classname "org.sqlite.JDBC",
         :subprotocol "sqlite",
         :subname "models.sq3"})


(defn create-termin-table []
  (sql/with-connection
    db
    (sql/create-table
      :termin
      [:id "INTEGER PRIMARY KEY AUTOINCREMENT"]
      [:vreme "TEXT"]
      [:tip "TEXT"]
      [:aktivnost "TEXT"]
      [:broj_igraca "INTEGER"]
      [:datum_rezervacije "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"])))


(defn read-termin []
  (sql/with-connection
    db
    (sql/with-query-results res
                            ["SELECT * FROM termin ORDER BY id ASC"]
                            (doall res))))


(defn save-termin [vreme tip aktivnost broj_igraca]
  (sql/with-connection
    db
    (sql/insert-values
      :termin
      [:vreme :tip :aktivnost :broj_igraca :datum_rezervacije]
      [vreme tip aktivnost broj_igraca (new java.util.Date)])))

(defn delete-termin [id]
  (sql/with-connection
    db
    (sql/delete-rows
      :termin
      ["id=?" id])))

(defn find-termin [id]
  (first
    (sql/with-connection
      db
      (sql/with-query-results res
                              ["SELECT * FROM termin WHERE id= ?" id]
                              (doall res)))))

(defn update-termin [id vreme tip aktivnost broj_igraca]
  (sql/with-connection
    db
    (sql/update-values
      :termin
      ["id=?" id]
      {:vreme vreme :tip tip :aktivnost aktivnost :broj_igraca broj_igraca})))
