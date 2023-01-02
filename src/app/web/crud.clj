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
   '[.mb-3
     [label.form-label {for (:id)}
      (:label)]
     [input.form-control {id  (:id)
                          ... (:input-attrs)}]]))

(defn input-presenter [path]
  (let [id   (path->input-id path)
        name (path->input-name path)]
    {::wt/renderable #'input-tmpl
     :id             id
     :label          (last path)
     :input-attrs    {:name name}}))


(def new-tmpl
  (wt/compile
   '[<>
     [form.needs-validation {action          (:action)
                             method          post
                             data-controller "form-validator"
                             data-action     "form-validator#validate"}
      (:string)
      (:required-string)
      (:number)
      [button.btn.btn-primary {type submit}
       "Submit"]]]))



(defn new-presenter [req]
  {::wt/renderable #'layout/layout-tmpl
   :body           {::wt/renderable  #'new-tmpl
                    :action          (path req :crud/new)
                    :string          (-> (input-presenter [:form :string])
                                         (update :input-attrs merge {:type "string"}))
                    :required-string (-> (input-presenter [:form :required-string])
                                         (update :input-attrs merge {:type     "string"
                                                                     :required true}))
                    :number          (-> (input-presenter [:form :number])
                                         (update :input-attrs merge {:type "text"
                                                                     :pattern #"\d+"}))}})

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
