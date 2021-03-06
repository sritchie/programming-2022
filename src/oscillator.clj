^{:nextjournal.clerk/visibility :hide-ns}
(ns oscillator
  (:refer-clojure
   :exclude [+ - * / = zero? compare
             numerator denominator ref partial])
  (:require [demo :as d]
            [nextjournal.clerk :as clerk]
            [physics-viewers :as pv]
            [sicmutils.env :as e :refer :all]))

;; ## Oscillator
;;
;; This is the Lagrangian for a harmonic oscillator, continued on from the
;; presentation.

(defn L-harmonic [m k]
  (fn [[_ [_ _ z :as q] v]]
    (let [T (* 1/2 m (square v))
          U (+ (* 1/2 k (square q))
               (* 9.8 m z))]
      (- T U))))

(def m 100)
(def k 200)
(def g 9.8)

(clerk/with-viewer (pv/physics-viewer 'mb/oscillator-demo)
  {:state->xyz coordinate
   :L (L-harmonic m k)
   :initial-state [0
                   [1 2 0]
                   [2 0 4]]
   :cartesian
   {:range [[-10, 10]
            [-10, 10]
            [-10, 10]]
    :scale [3 3 3]}})

;; ## Equations of Motion:

^{::clerk/visibility :hide}
(clerk/with-viewer d/multiviewer
  (let [L (L-harmonic 'm 'k)
        x (e/literal-function 'x)
        y (e/literal-function 'y)
        z (e/literal-function 'z)]
    (((e/Lagrange-equations L) (up x y z))
     't)))
