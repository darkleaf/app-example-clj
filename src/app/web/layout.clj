(ns app.web.layout
 (:require
  [darkleaf.web-template.core :as wt]))

(def layout-tmpl
  (wt/compile
   '[<>
     "<!doctype html>"
     [html {lang en}
      [head
       [meta {charset "utf-8"}]
       [meta {name          "viewport"
              content       "width=device-width"
              initial-scale "1"}]
       [title "App example"]

       "<!-- if development -->"
       [script {type module src "http://localhost:5173/assets/@vite/client"}]
       [script {type module src "http://localhost:5173/assets/main.js"}]]

      [body
       [.container
        (:body)]]]]))
