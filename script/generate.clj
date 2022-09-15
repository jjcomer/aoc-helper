(ns script.generate
  (:require [babashka.fs :as fs]
            [clojure.tools.cli :as cli]
            [clojure.string :as str]
            [script.util :as util]))

(defn generate-solution
  [solution-template year day]
  (let [safe-year (str "y" year)
        safe-day (str "d" day)
        solution-filename (str safe-day ".clj")
        solution-path (fs/path "src" safe-year)
        full-path (fs/path solution-path solution-filename)]
    (if (fs/exists? full-path)
      (println "Solution for year" year "and day" day "already exists")
      (do
        (when-not (fs/exists? solution-path)
          (fs/create-dir solution-path))
        (-> solution-template
            slurp
            (str/replace #"__YEAR__" year)
            (str/replace #"__DAY__" day)
            (#(spit (str full-path) %)))
        (println "Created template for year" year "and day" day "at" (str full-path))))))

(defn generate-solution-task
  [solution-template params]
  (let [{:keys [summary options errors]} (cli/parse-opts params util/general-input-params)]
    (cond
      (:help options)
      (println summary)

      (seq errors)
      (util/print-errors errors summary)

      :else
      (generate-solution solution-template (:year options) (:day options)))))



