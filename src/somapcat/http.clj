(ns somapcat.http
  "Communicate with Unix domain sockets via classical Ring request maps."
  (:require [clojure.java.io :as io]
            [byte-streams :as b]
            [somapcat.core :as s])
  (:import (java.io ByteArrayInputStream)
           (org.apache.http.impl.io DefaultHttpResponseParser
                                    SessionInputBufferImpl
                                    HttpTransportMetricsImpl)))

(defn header-map
  "Takes an array of Header objects and returns a map of header names to their
  values."
  [headers]
  (let [ks (map #(.getName %) headers)
        vs (map #(.getValue %) headers)]
    (zipmap ks vs)))

(defn read-session-input-buffer
  "Reads a SessionInputBufferImpl object bound to a ByteArrayInputStream.
  Returns a string representation of its contents."
  [buff]
  (loop [s []
         b buff]
    (if-not (.hasBufferedData buff)
      (apply str s)
      (recur (conj s (.readLine b))
             b))))

;; Inspired by http://stackoverflow.com/questions/9261109/is-there-any-simple-http-response-parser-for-java
(defn bytearray->httpresponse
  [b]
  (let [buff (SessionInputBufferImpl. (HttpTransportMetricsImpl.) 2048)
        bais (ByteArrayInputStream. b)]
    (doto buff
      (.bind bais))))

(defn parse-response
  "Given a byte array, returns a Ring repsonse map with its contents."
  [b]
  (let [buff (bytearray->httpresponse b)
        parser (DefaultHttpResponseParser. buff)
        parsed (.parse parser)
        headers (header-map (.getAllHeaders parsed))
        status (->> parsed
                    (.getStatusLine)
                    (.getStatusCode))
        body (read-session-input-buffer buff)]
    {:headers headers
     :status status
     :body body}))

(defn generate-query-string
  [scheme request-method query]
  (let [suffix "/1.1\r\n\n"
        scheme (-> scheme
                   (name)
                   (.toUpperCase)
                   (str suffix))]
     (str request-method " " query " " scheme)))

(defn request
  [{:keys [request-method scheme uri query-string] :as req}]
  (let [struct {:sun-family s/AF_UNIX
                :sun-path uri}
        message (generate-query-string scheme request-method query-string)]
    (-> (s/send struct message)
        b/to-byte-array
        parse-response)))

(defn get
  [uri query]
  (request {:request-method "GET"
            :scheme "HTTP"
            :uri uri
            :query-string query}))
