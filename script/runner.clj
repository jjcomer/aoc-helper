(ns script.runner
  (:require [clojure.tools.cli :as cli]
            [clojure.test :as test]
            [babashka.fs :as fs]
            [script.util :as util]
            [script.api :as api]))

(defn get-input
  [auth-file input-dir year day]
  (let [input-path (fs/path input-dir year (str day ".txt"))]
    (if (fs/exists? input-path)
      (slurp (str input-path))
      (do
        (api/download-input auth-file input-dir year day)
        (if (fs/exists? input-path)
          (slurp (str input-path))
          (throw (ex-info "Missing Input" {:day day :year year})))))))

(defn run-solution
  [auth-file input-dir year day]
  (let [input (get-input auth-file input-dir year day)
        solution-ns (util/gen-solution-ns year day)
        _ (require solution-ns)
        generator-fn (ns-resolve solution-ns 'generator)
        part1-fn (ns-resolve solution-ns 'solve-part-1)
        part2-fn (ns-resolve solution-ns 'solve-part-2)
        _ (println "Generating Input")
        parsed-input (time (generator-fn input))]
    (println)
    (println "PART 1 SOLUTION:")
    (let [part1-solution (time (part1-fn parsed-input))]
      (println part1-solution))
    (println)
    (println "PART 2 SOLUTION:")
    (let [part2-solution (time (part2-fn parsed-input))]
      (println part2-solution))))

(defn run-solution-task
  [auth-file input-dir params]
  (let [{:keys [options summary errors]} (cli/parse-opts params util/general-input-params)]
    (cond
      (:help options)
      (println summary)

      (seq errors)
      (util/print-errors errors summary)

      :else
      (run-solution auth-file input-dir (:year options) (:day options)))))

(defn run-tests
  [year day]
  (let [test-ns (util/gen-solution-ns year day)]
    (require test-ns)
    (test/run-tests test-ns)))

(defn run-tests-task
  [params]
  (let [{:keys [options summary errors]} (cli/parse-opts params util/general-input-params)]
    (cond
      (:help options)
      (println summary)

      (seq errors)
      (util/print-errors errors summary)

      :else
      (run-tests (:year options) (:day options)))))