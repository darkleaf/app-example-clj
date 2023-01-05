(ns app.system
  (:require
   [app.adapters.jetty :as jetty]
   [app.adapters.reitit :as reitit]
   [app.web.core :as web]
   [darkleaf.di.core :as di]))

(defn base-registry []
  [{::root              (di/ref `jetty/server)
    ::jetty/handler     (di/ref `reitit/handler)
    ::reitit/route-data (di/ref `web/route-data)}])

(defn dev-registry []
  [(base-registry)
   {"PORT"        "8080"
    "MONGODB_URI" "mongodb://root:example@localhost:27017"}])
