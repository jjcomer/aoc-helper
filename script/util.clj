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