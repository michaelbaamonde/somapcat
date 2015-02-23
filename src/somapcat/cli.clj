(ns somapcat.cli
  (:require [net.n01se.clojure-jna :as jna]
            [byte-streams :as b]
            [somapcat.http :as http]))

(defonce AF_UNIX 1)

(defonce SOCK_STREAM 1)

(def docker-socket "/var/run/docker.sock")

(def sock (jna/invoke Integer c/socket AF_UNIX SOCK_STREAM 0))

(def buff (jna/make-cbuf 22))

(.putShort buff (short 1))

(.put buff (.getBytes docker-socket))

(def received (jna/make-cbuf 2048))

(def message "GET /images/json HTTP/1.1\r\n\n")

(defn -main
  []
  (do
    (jna/when-err (jna/invoke Integer c/connect sock (jna/pointer buff) 22) "Sorry.")
    (println (jna/invoke Integer c/send sock message (.length message) 0))
    (jna/invoke Integer c/recv sock received 2048 0)
    (jna/invoke Integer c/close sock)
    (clojure.pprint/pprint (http/parse-response (b/to-byte-array received)))))
