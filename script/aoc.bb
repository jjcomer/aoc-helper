(ns aoc
  (:require [runner]
            [generate]
            [api]
            [util]
            [clojure.string :as str]
            [babashka.cli :as cli]))

(defn solve-task [input-dir auth-file args]
  (let [{:keys [help year day]} (cli/parse-opts args util/general-input-spec)]
    (if help
      (println (util/show-help util/general-input-spec))
      (runner/run-solution auth-file input-dir year day))))

(defn run-tests-task [args]
   (let [{:keys [help year day]} (cli/parse-opts args util/general-input-spec)]
    (if help
      (println (util/show-help util/general-input-spec))
      (runner/run-tests year day))))

(defn gen-new-task [solution-template args]
  (let [{:keys [help year day]} (cli/parse-opts args util/general-input-spec)]
    (if help
      (println (util/show-help util/general-input-spec))
      (generate/generate-solution solution-template year day))))

(defn get-input-task [auth-file input-dir args]
  (let [{:keys [help year day]} (cli/parse-opts args util/general-input-spec)]
    (if help
      (println (util/show-help util/general-input-spec))
      (api/download-input auth-file input-dir year day))))

(def store-auth-spec
  (util/gen-spec {:session {:alias :s
                            :validate #(not (str/blank? %))
                            :desc "Session key from browser cookie"
                            :require true}
                  :year {:desc "The year this session key is associated with. If not provided will default to current year"
                         :coerce :long
                         :alias :y}
                  :help {:coerce :boolean
                         :desc "Print out task help"}}
                 {:year (util/get-current-year)}))

(defn store-auth-task [auth-file args]
  (let [{:keys [help year session]} (cli/parse-opts args store-auth-spec)]
    (if help
      (println (util/show-help store-auth-spec))
      (api/store-auth auth-file year session))))