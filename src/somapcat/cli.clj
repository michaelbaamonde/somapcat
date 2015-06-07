(ns somapcat.cli
  (:require [somapcat.http :as http]))

(defn -main
  []
  (clojure.pprint/pprint
   (http/get "/var/run/docker.sock" "/info")))
