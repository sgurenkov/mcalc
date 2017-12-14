(ns user
  (:use [figwheel-sidecar.repl-api :as ra]))

(defn run []
  (ra/start-figwheel!)
  (ra/cljs-repl "dev"))

(defn stop [] (ra/stop-figwheel!))