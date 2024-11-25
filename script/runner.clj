(ns runner
  (:require [clojure.test :as test]
            [babashka.fs :as fs]
            [bling.core :as bling]
            [util]
            [api]))

(defn get-input
  [auth-file input-dir year day]
  (let [input-path (fs/path input-dir (str year) (str day ".txt"))]
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
        generator (util/time-execution (generator-fn input))
        parsed-input (:return generator)
        generator-time (:time generator)]
    (bling/callout {:type :info
                    :label "Input Generation"}
                   generator-time)
    (let [{:keys [return time]} (util/time-execution (part1-fn parsed-input))]
      (bling/callout {:type :positive
                      :label "Part 1 Solution"}
                     (format "Result: %s\n%s" return time)))

    (let [{:keys [return time]} (util/time-execution (part2-fn parsed-input))]
      (bling/callout {:type :positive
                      :label "Part 2 Solution"}
                     (format "Result: %s\n%s" return time)))))

(defn run-tests
  [year day]
  (let [test-ns (util/gen-solution-ns year day)]
    (require test-ns)
    (test/run-tests test-ns)))
