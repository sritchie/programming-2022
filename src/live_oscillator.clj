^{:nextjournal.clerk/visibility :hide-ns}
(ns live-oscillator
  (:refer-clojure
   :exclude [+ - * / = zero? compare
             numerator denominator ref partial])
  (:require [demo :as d]
            [nextjournal.clerk :as clerk]
            [physics-viewers :as pv]
            [sicmutils.env :as e :refer :all]))

;; ## Oscillator
;;
;; Now we come to a different style of thing.

(defn L-harmonic [m k g]
  (fn [[_ [_ _ z :as q] v]]
    (let [T (* 1/2 m (square v))
          U (+ (* 1/2 k (square q))
               (* g m z))]
      (- T U))))

(def m 100)
(def k 200)
(def g 9.8)

;; ## Live

(def init-state
  {:state->xyz coordinate
   :L (L-harmonic m k g)
   :initial-state [0
                   [1 2 0]
                   [2 0 4]]
   :cartesian
   {:range [[-10, 10]
            [-10, 10]
            [-10, 10]]
    :scale [3 3 3]}})

^{::clerk/viewer (pv/interactive-physics-viewer
                  'mb/oscillator-demo
                  (pv/physics-xform init-state))}
(defonce oscillator-state
  (atom init-state))

@oscillator-state

^{::clerk/visibility :hide}
(clerk/with-viewer (d/literal-viewer d/transform-literal)
  (let [L (L-harmonic 'm 'k 'g)
        x (e/literal-function 'x)
        y (e/literal-function 'y)
        z (e/literal-function 'z)]
    (((e/Lagrange-equations L) (up x y z))
     't)))
