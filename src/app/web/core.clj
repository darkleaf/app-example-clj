(ns app.web.core
  (:require
   [darkleaf.di.core :as di]
   [app.web.crud :as crud]))

(def route-data
  (di/template
   ["/crud" (di/ref `crud/route-data)]))
