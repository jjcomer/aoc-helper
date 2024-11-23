(ns api
  (:require [clojure.edn :as edn] 
            [babashka.fs :as fs] 
            [util]
            [babashka.http-client :as http]))

(defn read-auth
  [auth-file]
  (-> auth-file
      slurp
      edn/read-string))

(defn store-auth
  [auth-file year session-key]
  (let [current-auth (if (fs/exists? auth-file)
                       (-> auth-file
                           slurp
                           edn/read-string)
                       {})]
    (-> current-auth
        (assoc year session-key)
        pr-str
        (#(spit auth-file %)))))

(defn fetch-auth
  [auth-file year]
  (get (read-auth auth-file) year))

(def client (http/client (assoc-in http/default-client-opts [:ssl-context :insecure] true)))

(defn download-input
  [auth-file input-dir year day]
  (if-let [session (fetch-auth auth-file year)]
    (let [base-path (fs/path input-dir (str year))
          input-filename (str day ".txt")
          input-path (fs/path base-path input-filename)]
      (if (fs/exists? input-path)
        (println "Input for year" year "and day" day "has already been fetched")
        (let [url (format "https://adventofcode.com/%s/day/%s/input" year day)
              _ (println "Fetching:" url)
              input (http/get url {:headers {"Cookie" (str "session=" session)} 
                                   :user-agent "github.com/jjcomer/aoc-helper"
                                   :client client})]
          (when-not (fs/exists? base-path)
            (fs/create-dir base-path)) 
          (spit (str input-path) (:body input))
          (println "Input for year" year "and day" day "successfully pulled"))))
    (println "Missing auth for year:" year)))
