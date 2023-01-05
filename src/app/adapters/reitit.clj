(ns app.adapters.reitit
  (:require
   [reitit.ring]
   [ring.middleware.keyword-params :as ring.keyword-params]
   [ring.middleware.nested-params :as ring.nested-params]
   [ring.middleware.params :as ring.params]
   [ring.util.http-response :as ring.resp]))

(defn default-handler [-deps req]
  (ring.resp/not-found))

(def middleware [ring.params/wrap-params
                 ring.nested-params/wrap-nested-params
                 ring.keyword-params/wrap-keyword-params])

(defn handler [{route-data      ::route-data
                middleware      `middleware
                default-handler `default-handler}]
  (-> route-data
      (reitit.ring/router)
      (reitit.ring/ring-handler default-handler {:middleware middleware})))
