(ns app.web.crud
  (:require
   [app.adapters.mongo :as mongo]
   [app.web.layout :as layout]
   [clojure.string :as str]
   [darkleaf.di.core :as di]
   [darkleaf.web-template.core :as wt]
   [darkleaf.web-template.ring :as wt.ring]
   [reitit.core :as r]
   [ring.util.http-response :as ring.resp]
   [darkleaf.bson-clj.core :as bson])
  (:import
   (com.mongodb.client MongoClient MongoDatabase)
   (clojure.lang IPersistentMap)))

(set! *warn-on-reflection* true)

(defn- path [req name]
  (-> req
      ::r/router
      (r/match-by-name name)
      (r/match->path)))

(defn- db [{^MongoClient client `mongo/client}]
  (.. client
      (getDatabase "default")
      (withCodecRegistry (bson/codec-registry))))

(def index-tmpl
  (wt/compile
   '[<>
     [a.btn.btn-primary.mt-3 {href (:new-entity-url)}
      "New entity"]
     [table.table.table-bordered.mt-3
      [thead
       [tr
        [th "id"]
        [th "string"]]]
      [tbody
       (:entities
        [tr
         [td (:_id)]
         [td (:string)]])]]]))

(defn index-presenter [req entities]
  (layout/presenter req
    {::wt/renderable index-tmpl
     :new-entity-url (path req ::new)
     :entities       entities}))

(defn index-action [{^MongoDatabase db `db} req]
  (let [entities (.. db
                     (getCollection "crud" IPersistentMap)
                     (find))]

    (-> (wt.ring/body (index-presenter req entities))
        (ring.resp/ok)
        (ring.resp/content-type "text/html; charset=utf-8"))))

(def new-tmpl
  (wt/compile
   '[form.needs-validation {action          (:action)
                            method          post
                            data-controller "form-validator"
                            data-action     "form-validator#validate"}
     [.mb-3 {data-controller input data-input-t-key-value "string"}
      [label.form-label {for               crud_string
                         data-input-target label}]
      [input.form-control {id                crud_string
                           name              "crud[string]"
                           required          true
                           data-input-target input}]]
     [button.btn.btn-primary {type submit}
      "Submit"]]))

(defn new-presenter [req]
  (layout/presenter req
    {::wt/renderable new-tmpl
     :action         (path req ::index)}))

(defn new-action [-deps req]
  (-> (wt.ring/body (new-presenter req))
      (ring.resp/ok)
      (ring.resp/content-type "text/html; charset=utf-8")))

(defn create-action [{^MongoDatabase db `db} req]
  (.. db
      (getCollection "crud" IPersistentMap)
      (insertOne (-> req :params :crud)))
  (ring.resp/found (path req ::index)))

(def route-data
  (di/template
   [["" {:name ::index
         :get  (di/ref `index-action)
         :post (di/ref `create-action)}]
    ["/new" {:name ::new
             :get  (di/ref `new-action)}]]))
