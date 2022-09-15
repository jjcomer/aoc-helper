(ns script.util
  (:import java.time.LocalDate))

(defn get-current-month
  "Returns the numeric current month"
  []
  (.getMonthValue (LocalDate/now)))

(defn get-current-day
  "Returns the numeric value of the day"
  []
  (.getDayOfMonth (LocalDate/now)))

(defn get-current-year
  "Returns the current year"
  []
  (.getYear (LocalDate/now)))

(def general-input-params
  [["-y" "--year YEAR" "The year this input is associated with"
    :default (str (get-current-year))
    :validate [parse-long "Year must be an integer"]]
   ["-d" "--day DAY" "The day this input is associated with"
    :default (str (get-current-day))
    :validate [parse-long "Day must be an integer"
               #(<= 1 (parse-long %) 25) "Day must be between 1 and 25"]]
   ["-h" "--help"]])

(defn gen-solution-ns
  [year day]
  (symbol (str "y" year ".d" day)))

(defn print-errors
  [errors summary]
  (doseq [e errors]
    (println e))
  (println)
  (println summary))