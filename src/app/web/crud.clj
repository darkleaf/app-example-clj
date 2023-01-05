(ns app.web.crud
  (:require
   [app.web.layout :as layout]
   [darkleaf.di.core :as di]
   [darkleaf.web-template.core :as wt]
   [darkleaf.web-template.ring :as wt.ring]
   [reitit.core :as r]
   [ring.util.http-response :as ring.resp]
   [clojure.string :as str]))

(defn- path [req name]
  (-> req
      ::r/router
      (r/match-by-name name)
      (r/match->path)))

(def index-tmpl
  (wt/compile
   '[<>
     [a.btn.btn-primary {href (:new-entity-url)}
      "New entity"]]))

(defn index-presenter [req]
  {::wt/renderable layout/layout-tmpl
   :body           {::wt/renderable index-tmpl
                    :new-entity-url (path req ::new)}})

(defn index-action [-deps req]
  (-> (wt.ring/body (index-presenter req))
      (ring.resp/ok)
      (ring.resp/content-type "text/html; charset=utf-8")))

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
  {::wt/renderable layout/layout-tmpl
   :body           {::wt/renderable new-tmpl
                    :action         (path req ::index)}})

(defn new-action [-deps req]
  (-> (wt.ring/body (new-presenter req))
      (ring.resp/ok)
      (ring.resp/content-type "text/html; charset=utf-8")))

(defn create-action [-deps req]
  (let [form (-> req
                 :params
                 :crud)]
    (prn form)))

(def route-data
  (di/template
   [["" {:name ::index
         :get  (di/ref `index-action)
         :post (di/ref `create-action)}]
    ["/new" {:name ::new
             :get  (di/ref `new-action)}]]))
