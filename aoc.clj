(ns aoc
  (:require [script.runner :as runner]
            [script.generate :as generate]
            [script.api :as api]
            [script.util :as util]))

(defn run-solution [{:keys [input-dir auth-file year day]
                     :or {year (util/get-current-year)
                          day (util/get-current-day)}}]
  (runner/run-solution (str auth-file)
                       (str input-dir)
                       (str year)
                       (str day)))

(defn run-tests [{:keys [year day]
                  :or {year (util/get-current-year)
                       day (util/get-current-day)}}]
  (runner/run-tests (str year) (str day)))

(defn gen-new [{:keys [year day solution-template]
                :or {solution-template "script/solution_template.clj"
                     year (util/get-current-year)
                     day (util/get-current-day)}}]
  (generate/generate-solution (str solution-template)
                              (str year)
                              (str day)))

(defn get-input [{:keys [auth-file input-dir year day]
                  :or {year (util/get-current-year)
                       day (util/get-current-day)}}]
  (api/download-input (str auth-file)
                      (str input-dir)
                      (str year)
                      (str day)))

(defn store-auth [{:keys [auth-file args]}]
  (let [args (apply hash-map args)]
    (api/store-auth (str auth-file)
                    (str (get args ":year" (util/get-current-year)))
                    (str (get args ":session")))))