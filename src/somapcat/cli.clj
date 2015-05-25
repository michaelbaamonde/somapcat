(ns somapcat.cli
  (:require [byte-streams :as b]
            [somapcat.core :as s]
            [somapcat.http :as http]))

(def docker-socket "/var/run/docker.sock")

(def message "GET /info HTTP/1.1\r\n\n")

(defn -main
  []
  (clojure.pprint/pprint
   (http/request {:request-method "GET"
                  :scheme "HTTP"
                  :uri "/var/run/docker.sock"
                  :query-string "/info"})))
