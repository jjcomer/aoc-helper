(ns util
  (:import 'java.time.LocalDateTime))

(defn get-current-month
  "Returns the numeric current month"
  []
  (.getMonthValue (LocalDateTime/now)))

(defn get-current-day
  "Returns the numeric value of the day"
  []
  (.getDayOfMonth (LocalDateTime/now)))

(defn get-current-year
  "Returns the current year"
  []
  (.getYear (LocalDateTime/now)))

(def general-input-params
  [["-y" "--year YEAR" "The year this input is associated with"
    :default (str (util/get-current-year))
    :validate [parse-long "Year must be an integer"]]
   ["-d" "--day DAY" "The day this input is associated with"
    :default (str (util/get-current-day))
    :validate [parse-long "Day must be an integer"
               #(<= 1 (parse-long %) 25) "Day must be between 1 and 25"]]
   ["-h" "--help"]])