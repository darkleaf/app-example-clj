(ns user
  (:require
   [app.system :as s]
   [darkleaf.di.core :as di]))

(defonce system (atom nil))

(defn start []
  (reset! system (di/start ::s/root (s/dev-registry))))

(defn stop []
  (di/stop @system))

(defn restart []
  (stop)
  (start))

(comment
  (restart)
  nil)
