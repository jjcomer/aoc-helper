(ns aoc
  (:require [script.runner :as runner]
            [script.generate :as generate]
            [script.api :as api]))

(defn run-solution [{:keys [input-dir auth-file year day]}]
  (runner/run-solution (str auth-file)
                       (str input-dir)
                       (str year)
                       (str day)))

(defn run-tests [{:keys [year day]}]
  (runner/run-tests (str year) (str day)))

(defn gen-new [{:keys [year day solution-template]
                :or {solution-template "script/solution_template.clj"}}]
  (generate/generate-solution (str solution-template)
                              (str year)
                              (str day)))

(defn get-input [{:keys [auth-file input-dir year day]}]
  (api/download-input (str auth-file)
                      (str input-dir)
                      (str year)
                      (str day)))

(defn store-auth [{:keys [auth-file year session]}]
  (api/store-auth (str auth-file)
                  (str year)
                  (str session)))