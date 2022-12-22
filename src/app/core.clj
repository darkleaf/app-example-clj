(ns app.core
  (:require
   [darkleaf.di.core :as di]
   [darkleaf.web-template.core :as wt]
   [darkleaf.web-template.protocols :as wtp]
   [ring.util.http-response :as ring.resp]))


;; ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~



(def body-tmpl
  (wt/compile
   '[form
     [input {value "1"}]]))

(defn handler [-deps req]
  (let []
    (-> (wt/render-to-string #'layout {:body #'body-tmpl})
        (ring.resp/ok))))
