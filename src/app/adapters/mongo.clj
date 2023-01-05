(ns app.adapters.mongo
  (:require
   [darkleaf.di.core :as di])
  (:import
   (com.mongodb.client MongoClients)))

(defn client
  {::di/stop #(.close %)}
  [{uri "MONGODB_URI"}]
  (MongoClients/create uri))
