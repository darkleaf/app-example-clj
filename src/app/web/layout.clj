(ns app.web.layout
 (:require
  [darkleaf.web-template.core :as wt]))

(def tmpl
  (wt/compile
   '[<>
     "<!doctype html>"
     [html {lang en}
      [head
       [meta {charset "utf-8"}]
       [meta {name          "viewport"
              content       "width=device-width"
              initial-scale "1"}]

       [meta {name "csrf-token" content (:csrf-token)}]
       [title "App example"]

       "<!-- if development -->"
       [script {type module src "/assets/@vite/client"}]
       [script {type module src "/assets/main.js"}]]

      [body
       [.container
        (:body)]]]]))

(defn presenter
  {:style/indent :defn}
  [req body]
  {::wt/renderable tmpl
   :csrf-token     (-> req :anti-forgery-token)
   :body           body})
