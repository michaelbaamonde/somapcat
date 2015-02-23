(ns somapcat.http
  "Communicate with Unix domain sockets via classical Ring request maps."
  (:require [clojure.java.io :as io]
            [byte-streams :as b])
  (:import (java.io ByteArrayInputStream InputStream IOException)
           (org.apache.http.impl.io DefaultHttpResponseParser
                                    SessionInputBufferImpl
                                    HttpTransportMetricsImpl)))

(defn header-map
  [headers]
  (let [ks (map #(.getName %) headers)
        vs (map #(.getValue %) headers)]
    (zipmap ks vs)))

(defn read-session-input-buffer
  [buff]
  (loop [s []
         b buff]
    (if-not (.hasBufferedData buff)
      (apply str s)
      (recur (conj s (.readLine b))
             b))))

;; Inspired by http://stackoverflow.com/questions/9261109/is-there-any-simple-http-response-parser-for-java
(defn parse-response
  "Given a byte array, returns a Ring repsonse map with its contents."
  [b]
  (let [buff (SessionInputBufferImpl. (HttpTransportMetricsImpl.) 2048)
        bais (ByteArrayInputStream. b)]
    (do
      (.bind buff bais))
      (let [parser (DefaultHttpResponseParser. buff)
            parsed (.parse parser)
            headers (header-map (.getAllHeaders parsed))
            status (->> parsed
                        (.getStatusLine)
                        (.getStatusCode))
            body (read-session-input-buffer buff)]
        {:headers headers
         :status status
         :body body})))

(defn request->string
  "Given a Ring request map, returns a string representation of it suitable for
  passing to struct-to-byte-buffer."
  [scheme request-method uri]
  (let [ending "/1.1\r\n\n"
        scheme (-> scheme
                   (name)
                   (.toUpperCase)
                   (str ending))]
     (str request-method " " uri " " scheme)))
