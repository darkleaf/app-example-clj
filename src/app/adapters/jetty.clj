(ns app.adapters.jetty
  (:require
   [darkleaf.di.core :as di]
   [ring.adapter.jetty :as jetty]))

(def port (-> (di/ref "PORT")
              (di/fmap parse-long)))

(defn server
  {::di/stop #(.stop %)}
  [{handler ::handler
    port    `port}]
  (jetty/run-jetty handler {:join? false, :port  port}))
