(ns generate
  (:require [babashka.fs :as fs]
            [clojure.tools.cli :as cli]
            [util]))

(defn generate-solution
  [solution-template year day])

(defn generate-solution-task
  [solution-template params]
  (let [{:keys [summary options errors]} (cli/parse-opts params util/general-input-params)]
    (cond
      (:help options)
      (println summary)

      (seq errors)
      (do
        (doseq [e errors]
          (println "ERROR:" e))
        (println "\n" summary))

      :else
      (generate-solution solution-template (:year params) (:day params)))))



