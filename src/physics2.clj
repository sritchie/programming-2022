^{:nextjournal.clerk/visibility :hide-ns}
(ns physics2
  (:refer-clojure
   :exclude [+ - * / = zero? compare
             numerator denominator ref partial])
  (:require [nextjournal.clerk :as clerk]
            [physics :as phys]
            [sicmutils.env :as e :refer :all]))

;; ## Physics Example, copy of physics namespace

(defn elliptical->rect [a b c]
  (fn [[_ [theta phi theta2 phi2] _]]
    (e/up (e/* a (e/sin theta) (e/cos phi))
          (e/* b (e/sin theta) (e/sin phi))
          (e/* c (e/cos theta))
          (e/* a (e/sin theta2) (e/cos phi2))
          (e/* b (e/sin theta2) (e/sin phi2))
          (e/* c (e/cos theta2)))))

;; Next, the Lagrangian:

(defn L-central-triaxial [m a b c]
  (comp (- (phys/L-free-particle m)
           ;; POTENTIAL ENERGY TERM!!!!
           (fn [[_ [x1 y1 z1 x2 y2 z2]]]
             (let [k 5]
               (* (e// 1 2)
                  k
                  (square
                   (- [x1 y1 z1]
                      [x2 y2 z2]))))))
        (e/F->C (elliptical->rect a b c))))

;; Final Lagrangian:

(clerk/with-viewer (phys/physics-viewer 'mb/double-physics-demo)
  (let [m 10, a 2, b 1, c 1]
    {:degrees-of-freedom 4
     :state->xyz (elliptical->rect a b c)
     :L          (L-central-triaxial m a b c)
     :initial-state [0 [0.1 0.1
                        2 2]
                     [0.3 0.3 0 0]]
     :ellipse {:a a :b b :c c}
     :cartesian
     {:range [[-10, 10]
              [-10, 10]
              [-10, 10]]
      :scale [3 3 3]}}))
