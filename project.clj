(defproject somapcat "0.1.0-SNAPSHOT"
  :description "HTTP over Unix domain sockets."
  :url "http://github.com/michaelbaamonde/somapcat"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [net.n01se/clojure-jna "1.0.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.apache.httpcomponents/httpcore "4.4"]
                 [byte-streams "0.2.0-alpha8"]
                 [nio "1.0.3"]])
