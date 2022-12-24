(ns app.web.crud
  (:require
   [app.web.layout :as layout]
   [darkleaf.di.core :as di]
   [darkleaf.web-template.core :as wt]
   [darkleaf.web-template.ring :as wt.ring]
   [reitit.core :as r]
   [ring.util.http-response :as ring.resp]))

;; fail fast
(defn text [#_{:required true, min max}]
  (fn [x]
    x))

(defn coerce [params schema]
  (persistent!
   (reduce-kv (fn [acc key coercer]
                (let [key-name (name key)
                      value    (get params key-name)
                      ;;todo: coercer as map
                      value    (coercer value)]
                  (assoc! acc key value)))
              (transient {})
              schema)))

(def index-tmpl
  (wt/compile
   '[<>
     [a.btn.btn-primary {href (:new-entity-url)}
      "New entity"]]))

(defn index-presenter [{::r/keys [router]}]
  {::wt/renderable #'layout/layout-tmpl
   :body           {::wt/renderable #'index-tmpl
                    :new-entity-url (-> router
                                        (r/match-by-name ::new)
                                        (r/match->path))}})

(defn index-action [-deps req]
  (-> (wt.ring/body (index-presenter req))
      (ring.resp/ok)
      (ring.resp/content-type "text/html; charset=utf-8")))

(defn show-action [-deps req])

(def input-tmpl
  (wt/compile
   '[.mb-3
     [label.form-label {for (:id)}
      (:label)]
     [input.form-control {id  (:id)
                          ... (:input-attrs)}]]))

(def new-tmpl
  (wt/compile
   '[<>
     [form.needs-validation {action     (:action)
                             method     post
                             novalidate true}
      (:string)
      (:required-string)
      [button.btn.btn-primary {type submit}
       "Submit"]]
     [script
      "// Example starter JavaScript for disabling form submissions if there are invalid fields
(() => {
  'use strict'

  // Fetch all the forms we want to apply custom Bootstrap validation styles to
  const forms = document.querySelectorAll('.needs-validation')

  // Loop over them and prevent submission
  Array.from(forms).forEach(form => {
    form.addEventListener('submit', event => {
      if (!form.checkValidity()) {
        event.preventDefault()
        event.stopPropagation()
      }

      form.classList.add('was-validated')
    }, false)
  })
})()"]]))

(defn new-presenter [{::r/keys [router]}]
  {::wt/renderable #'layout/layout-tmpl
   :body           {::wt/renderable  #'new-tmpl
                    :action          (-> router
                                         (r/match-by-name ::index)
                                         (r/match->path))
                    :string          {::wt/renderable #'input-tmpl
                                      :id             "string"
                                      :label          "String"
                                      :input-attrs    {:type "text"
                                                       :name "form[string]"}}
                    :required-string {::wt/renderable #'input-tmpl
                                      :id             "required-string"
                                      :label          "Required string"
                                      :input-attrs    {:type     "text"
                                                       :required true
                                                       :name     "form[required-string]"}}}})

(defn new-action [-deps req]
  (-> (wt.ring/body (new-presenter req))
      (ring.resp/ok)
      (ring.resp/content-type "text/html; charset=utf-8")))

(defn create-action [-deps req]
  (let [form (-> req
                 :params
                 (get "form")
                 (coerce {:string          (text)
                          :required-string (text)}))]
    (prn form)))

(def route-data
  (di/template
   [["" {:name ::index
         :get  (di/ref `index-action)
         :post (di/ref `create-action)}]
    ["/new" {:name ::new
             :get  (di/ref `new-action)}]]))
