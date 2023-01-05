(ns app.adapters.reitit
  (:require
   [reitit.ring]
   [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.nested-params :refer [wrap-nested-params]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.session :refer [wrap-session]]
   [ring.middleware.session.cookie :refer [cookie-store]]
   [ring.util.http-response :as ring.resp]))

(defn default-handler [-deps req]
  (ring.resp/not-found))

(def middleware [[wrap-session {:store (cookie-store)}]
                 wrap-anti-forgery
                 wrap-params
                 wrap-nested-params
                 wrap-keyword-params])

(defn handler [{route-data      ::route-data
                middleware      `middleware
                default-handler `default-handler}]
  (-> route-data
      (reitit.ring/router)
      (reitit.ring/ring-handler default-handler {:middleware middleware})))
