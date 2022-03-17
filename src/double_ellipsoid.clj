^{:nextjournal.clerk/visibility :hide-ns}
(ns double-ellipsoid
  (:refer-clojure
   :exclude [+ - * / = zero? compare
             numerator denominator ref partial])
  (:require [demo :as d]
            [nextjournal.clerk :as clerk]
            [ellipsoid :as ell]
            [physics-viewers :as pv]
            [sicmutils.env :as e :refer :all]))

;; ## Ellipsoid with Two

(defn elliptical->rect [a b c]
  (fn [[_ [theta phi theta2 phi2] _]]
    (up (* a (sin theta) (cos phi))
        (* b (sin theta) (sin phi))
        (* c (cos theta))
        (* a (sin theta2) (cos phi2))
        (* b (sin theta2) (sin phi2))
        (* c (cos theta2)))))

;; Next, the Lagrangian:

(defn L-central-triaxial [m a b c]
  (comp (- (ell/L-free-particle m)
           ;; POTENTIAL ENERGY TERM!!!!
           (fn [[_ [x1 y1 z1 x2 y2 z2]]]
             (let [k  10
                   x0 0.3
                   x  (abs
                       (- [x1 y1 z1]
                          [x2 y2 z2]))]
               (* (/ 1 2)
                  k
                  (square (- x x0))))))
        (F->C (elliptical->rect a b c))))

;; Final Demo:

(clerk/with-viewer (pv/physics-viewer 'mb/double-physics-demo)
  (let [m 10, a 2, b 1, c 1]
    {:state->xyz (elliptical->rect a b c)
     :L          (L-central-triaxial m a b c)
     :initial-state [0 [0.1 0.1 2 2]
                     [0.3 0.3 0 0]]
     :ellipse {:a a :b b :c c}
     :cartesian
     {:range [[-10, 10]
              [-10, 10]
              [-10, 10]]
      :scale [3 3 3]}}))

;; ### The equations of Motion are too extreme!
