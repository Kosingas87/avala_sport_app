(ns avala-sport-app.home-test
  (:require [balon.routes.home :refer :all]
            [midje.sweet :refer [facts throws => roughly truthy just]]
            ))

(facts
  "Test format time funkcije"
  (format-time 1514827106687) => "01/01/2018")



