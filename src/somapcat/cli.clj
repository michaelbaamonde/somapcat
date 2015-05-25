(ns somapcat.cli
  (:require [byte-streams :as b]
            [somapcat.core :as s]
            [somapcat.http :as http]))

(defn -main
  []
  (clojure.pprint/pprint
   (http/request {:request-method "GET"
                  :scheme "HTTP"
                  :uri "/var/run/docker.sock"
                  :query-string "/info"})))
