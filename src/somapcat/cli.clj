(ns somapcat.cli
  (:require [byte-streams :as b]
            [somapcat.http :as http]))

(defn -main
  []
  (clojure.pprint/pprint
   (http/get "/var/run/docker.sock" "/info")))
