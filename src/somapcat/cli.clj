(ns somapcat.cli
  (:require [net.n01se.clojure-jna :as jna]
            [byte-streams :as b]
            [somapcat.core :as s]
            [somapcat.http :as http]))

(defonce AF_UNIX 1)

(defonce SOCK_STREAM 1)

(def docker-socket "/var/run/docker.sock")

(def message "GET /images/json HTTP/1.1\r\n\n")

(defn -main
  []
  (let [struct {:sun-family AF_UNIX
                :sun-path docker-socket}
        bytes (s/send struct message)]
      (clojure.pprint/pprint (http/parse-response (b/to-byte-array bytes)))))
