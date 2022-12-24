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
       [link {href        "https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css"
              rel         "stylesheet"
              integrity   "sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65"
              crossorigin "anonymous"}]
       #_
       [script {src         "https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js"
                integrity   "sha384-oBqDVmMz9ATKxIep9tiCxS/Z9fNfEXiDAYTujMAeBAsjFuCZSmKbSSUnQlmh/jp3"
                crossorigin "anonymous"}]
       [script {src         "https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.min.js"
                integrity   "sha384-cuYeSxntonz0PPNlHhBs68uyIAVpIIOZZ5JqeqvYYIcEL727kskC66kF92t6Xl2V"
                crossorigin "anonymous"}]]
      [body
       [.container
        (:body)]]]]))
