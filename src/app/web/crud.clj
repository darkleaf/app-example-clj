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

(defn- ->str [x]
  (cond
    (keyword? x) (-> x symbol str)
    :else (str x)))

(defn- path->input-name [path]
  (let [[head & tail] path]
    (str
     (->str head)
     (->> tail
          (map #(str "[" (->str %) "]"))
          (str/join)))))

(defn- path->input-id [path]
  (->> path (map ->str) (str/join "_")))

(def index-tmpl
  (wt/compile
   '[<>
     [a.btn.btn-primary {href (:new-entity-url)}
      "New entity"]]))

(defn index-presenter [req]
  {::wt/renderable #'layout/layout-tmpl
   :body           {::wt/renderable #'index-tmpl
                    :new-entity-url (path req :crud/new)}})

(defn index-action [-deps req]
  (-> (wt.ring/body (index-presenter req))
      (ring.resp/ok)
      (ring.resp/content-type "text/html; charset=utf-8")))


(def input-tmpl
  (wt/compile
   '[.mb-3 {data-controller        "input"
            data-input-t-key-value (:t-key)}
     [label.form-label {for               (:id)
                        data-input-target "label"}
      (:label)]
     [input.form-control {id                (:id)
                          data-input-target "input"
                          ...               (:input-attrs)}]]))

(defn input-presenter [form field]
  (let [id         (str/join "_" [(name form) (name field)])
        t-key      (str/join "." ["forms" (name form) (name field)])
        input-name (str (name form) "[" (name field) "]")]
    {::wt/renderable #'input-tmpl
     :t-key          t-key
     :id             id
     :input-attrs    {:name input-name}}))

(def new-tmpl
  (wt/compile
   '[form.needs-validation {action          (:action)
                            method          post
                            data-controller "form-validator"
                            data-action     "form-validator#validate"}
     (:string)
     (:required-string)
     (:number)
     [button.btn.btn-primary {type submit}
      "Submit"]]))


(defn new-presenter [req]
  {::wt/renderable #'layout/layout-tmpl
   :body           {::wt/renderable  #'new-tmpl
                    :action          (path req :crud/new)
                    :string          (input-presenter :crud :string)
                    :required-string (input-presenter :crud :required-string)
                    :number          (input-presenter :crud :number)}})

(defn new-action [-deps req]
  (-> (wt.ring/body (new-presenter req))
      (ring.resp/ok)
      (ring.resp/content-type "text/html; charset=utf-8")))

(defn create-action [-deps req]
  (let [form (-> req
                 :params
                 (get "form"))]
    (prn form)))

(def route-data
  (di/template
   [["" {:name :crud/index
         :get  (di/ref `index-action)}]

    ["/new" {:name :crud/new
             :get  (di/ref `new-action)
             :post (di/ref `create-action)}]]))
