(ns somapcat.core
  (:require [net.n01se.clojure-jna :as jna]
            [byte-streams :as b]))

;; TODO timeouts, with-connection macro (?), async requests

(defonce AF_UNIX 1)

(defonce SOCK_STREAM 1)

(defn struct-to-byte-buffer
  "Takes a 'struct' (really, a map) of the following form:

  {:sun-family AF_UNIX
   :sun-path \"/var/run/docker/sock\"}

  and returns a pointer to a DirectByteBuffer with the struct's contents.
  Does not pad memory."
  [s]
  (let [family (short (:sun-family s))
        path (.getBytes (:sun-path s))
        n (+ 2 (.length (:sun-path s)))
        buff (jna/make-cbuf n)]
    (-> buff
        (.putShort family)
        (.put path)
        (jna/pointer))))

(defn create-socket
  [family type flags]
  (jna/when-err
   (jna/invoke Integer c/socket family type flags)
   "Unable to create socket."))

;; TODO: Make this take a destination string as opposed to a 'struct.'
(defn connect
  [socket struct]
  (let [ptr (struct-to-byte-buffer struct)
        n (+ (.length (:sun-path struct)) 2)]
    (jna/when-err
     (jna/invoke Integer c/connect socket ptr n)
     "Unable to connect.")))

(defn send* [socket message]
  (jna/when-err
   (jna/invoke Integer c/send socket message (.length message) 0)
   "Unable to send message."))

(defn receive
  [socket buff]
   (let [n (.capacity buff)]
     (jna/when-err
      (jna/invoke Integer c/recv socket buff n 0)
      "Unable to receive message.")))

(defn close
  [sock]
  (jna/when-err
   (jna/invoke Integer c/close sock)
   "Unable to close socket."))

; TODO: Optionally leave socket open.
(defn send
  "Two-arity creates a *new* socket, connects to a given address, sends the
  message, and returns a DirectByteBuffer with the response. Three-arity does
  the same but takes an existing socket as an argument."
  ([struct message]
   (let [socket (create-socket AF_UNIX SOCK_STREAM 0)]
     (send struct message socket)))
  ([struct message socket]
   (let [buff (jna/make-cbuf 2048)]
     (do (connect socket struct)
         (send* socket message)
         (receive socket buff)
         (close socket))
     buff)))
