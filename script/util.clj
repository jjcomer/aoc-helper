(ns util
  (:import java.time.LocalDate)
  (:require [babashka.cli :as cli]
            [clojure.string :as str]))

(defn get-current-day
  "Returns the numeric value of the day"
  []
  (.getDayOfMonth (LocalDate/now)))

(defn get-current-year
  "Returns the current year"
  []
  (.getYear (LocalDate/now)))

(defn gen-solution-ns
  [year day]
  (symbol (str "y" year ".d" day)))

(defn show-help
  [spec]
  (cli/format-opts (merge spec {:order (vec (keys (:spec spec)))})))

(defn gen-spec [spec defaults]
  {:spec spec
   :exec-args defaults
   :error-fn (fn [{:keys [spec type cause msg option] :as data}]
               (if (= :org.babashka/cli type)
                 (case cause
                   :require
                   (println
                    (format "Missing required argument:\n%s\n\n%s"
                            (cli/format-opts {:spec (select-keys spec [option])})
                            (show-help {:spec spec})))
                   (println (format "%s\n\n%s" msg (show-help {:spec spec}))))
                 (throw (ex-info msg data)))
               (System/exit 1))})

(def general-input-spec
  (gen-spec {:year {:alias :y
                    :coerce :long
                    :desc "The year this input is associated with. Defaults to current year"}
             :day {:alias :d
                   :coerce :long
                   :desc "The day this input is associated with. Defaults to the current day"
                   :validate #(<= 1 % 25)}
             :help {:alias :h}}
            {:day (get-current-day)
             :year (get-current-year)}))

(defmacro time-execution
  [& body]
  `(let [s# (new java.io.StringWriter)]
     (binding [*out* s#]
       (hash-map :return (time ~@body)
                 :time   (str/trim (str s#))))))
