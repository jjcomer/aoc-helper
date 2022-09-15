(ns script.api
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [babashka.fs :as fs]
            [clojure.tools.cli :as cli]
            [script.util :as util]
            [org.httpkit.client :as http]))

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
  (get (read-auth auth-file) (str year)))

(defn download-input
  [auth-file input-dir year day]
  (if-let [session (fetch-auth auth-file year)]
    (let [base-path (fs/path input-dir year)
          input-filename (str day ".txt")
          input-path (fs/path base-path input-filename)]
      (if (fs/exists? input-path)
        (println "Input for year" year "and day" day "has already been fetched")
        (let [url (format "https://adventofcode.com/%s/day/%s/input" year day)
              _ (println "Fetching:" url)
              input @(http/get url {:headers {"Cookie" (str "session=" session)}})]
          (when-not (fs/exists? base-path)
            (fs/create-dir base-path))
          (spit (str input-path) (:body input))
          (println "Input for year" year "and day" day "successfully pulled"))))
    (println "Missing auth for year:" year)))

(defn download-input-task [auth-file input-dir params]
  (let [{:keys [options summary errors]} (cli/parse-opts params util/general-input-params)]
    (cond
      (:help options)
      (println summary)

      (seq errors)
      (util/print-errors errors summary)

      :else
      (download-input auth-file input-dir (:year options) (:day options)))))

(def store-auth-params
  [["-s" "--session SESSION" "Session key from browser cookie"
    :required true
    :validate [#(not (str/blank? %)) "Session key must not be blank"]]
   ["-y" "--year YEAR" "The year this session key is associated with"
    :default (str (util/get-current-year))]
   ["-h" "--help"]])

(defn store-auth-task [auth-file params]
  (let [{:keys [options summary errors]} (cli/parse-opts params store-auth-params)]
    (cond
      (:help options)
      (println summary)

      (seq errors)
      (util/print-errors errors summary)

      :else
      (store-auth auth-file (:year options) (:session options)))))