(ns app.core
  (:require
   [darkleaf.di.core :as di]
   [darkleaf.web-template.core :as wt]
   [darkleaf.web-template.protocols :as wtp]
   [ring.adapter.jetty :as jetty]
   [ring.util.http-response :as ring.resp]))


(defonce system (atom nil))

(defn start []
  (reset! system (di/start `server {"PORT" "8080"})))

(defn stop []
  (di/stop @system))

(defn restart []
  (stop)
  (start))

(comment
  (restart)
  nil)


;; ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

(def port (-> (di/ref "PORT")
              (di/fmap parse-long)))

(defn server
  {::di/stop #(.stop %)}
  [{handler `handler
    port    `port}]
  (jetty/run-jetty handler {:join? false
                            :port  port}))

;; ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

(def layout
  (wt/compile
   '[<>
     "<!doctype html>"
     [html {lang en}
      [head
       [meta {charset "utf-8"}]
       [meta {name          "viewport"
              content       "width=device-width"
              initial-scale "1"}]
       [title "Bootstrap demo"]
       [link {href        "https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css"
              rel         "stylesheet"
              integrity   "sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65"
              crossorigin "anonymous"}]]
      [body
       [nav.navbar.navbar-expand-lg.bg-light
        [.container
         [a.navbar-brand {href "#"}
          "Twitter clone clj"]
         [button.navbar-toggler {type           "button"
                                 data-bs-toggle "collapse"
                                 data-bs-target "#navbarSupportedContent"
                                 aria-controls  "navbarSupportedContent"
                                 aria-expanded  "false"
                                 aria-label     "Toggle navigation"}
          [span.navbar-toggler-icon]]
         [.collapse.navbar-collapse#navbarSupportedContent
          [ul.navbar-nav.me-auto.mb-2.mb-lg-0
           [li.nav-item
            [a.nav-link.active {aria-current "page"
                                href         "#"}
             "Home"]]]]]]

       (:body)

       [script {src         "https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js"
                integrity   "sha384-oBqDVmMz9ATKxIep9tiCxS/Z9fNfEXiDAYTujMAeBAsjFuCZSmKbSSUnQlmh/jp3"
                crossorigin "anonymous"}]
       [script {src         "https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.min.js"
                integrity   "sha384-cuYeSxntonz0PPNlHhBs68uyIAVpIIOZZ5JqeqvYYIcEL727kskC66kF92t6Xl2V"
                crossorigin "anonymous"}]]]]))

(def body-tmpl
  (wt/compile
   '[.container
     (:new-tweet (:tmpl))
     (:tweets (:tmpl))]))

(def tweet-tmpl
  (wt/compile
   '[.card.my-4
     [.card-body
      (:text)]
     [.card-footer
      #_(:created-at)
      [a {href (:author-url)}
       (:author)]
      #_(:likes)]]))

(defn tweet-presenter [tweet]
  (-> tweet
      (assoc :tmpl #'tweet-tmpl)
      (assoc :author-url "#")))

(def new-tweet-tmpl
  (wt/compile
   '[.card.my-4
     [.card-body
      [form
       [.mb-3
        [textarea.form-control {rows 3}]]
       [button.btn.btn-primary {type submit}
        "Tweet"]]]]))

(defn new-tweet-presenter []
  {:tmpl #'new-tweet-tmpl})

(defn handler [-deps req]
  (let [tweets [{:text   "awesome"
                 :author "@john"}
                {:text   "Hi"
                 :author "@jane"}]]

    (-> (wt/render-to-string #'layout {:body      #'body-tmpl
                                       :new-tweet (new-tweet-presenter)
                                       :tweets    (map tweet-presenter tweets)})


        (ring.resp/ok))))
