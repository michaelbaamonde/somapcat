(ns somapcat.cli
  (:require [byte-streams :as b]
            [somapcat.core :as s]
            [somapcat.http :as http]))

(def docker-socket "/var/run/docker.sock")

(def message "GET /info HTTP/1.1\r\n\n")

(defn -main
  []
  (let [struct {:sun-family s/AF_UNIX
                :sun-path docker-socket}
        bytes (s/send struct message)]
      (clojure.pprint/pprint (http/parse-response (b/to-byte-array bytes)))))
