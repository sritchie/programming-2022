^{:nextjournal.clerk/visibility :hide-ns}
(ns physics2
  (:refer-clojure
   :exclude [+ - * / = zero? compare
             numerator denominator ref partial])
  (:require [demo :as d]
            [functions :as fs]
            [nextjournal.clerk :as clerk]
            [sicmutils.env :as e :refer :all]))

;; ## Physics Example, copy of physics namespace
;;
;; This is a copy-paste, tuned for two beads.

;; > A bead of mass $m$ moves without friction on a triaxial ellipsoidal
;; > surface. In rectangular coordinates the surface satisfies
;; >
;; > $$
;; {x^2 \over a^2} + {y^2 \over b^2} + {z^2 \over c^2} = 1
;; $$
;; >
;; > for some constants $a$, $b$, and $c$. Identify suitable generalized
;; > coordinates, formulate a Lagrangian, and find Lagrange's equations.
;;
;; The transformation to elliptical coordinates is very similar to the spherical
;; coordinate transformation, but with a fixed $a$, $b$ and $c$ coefficient for
;; each rectangular dimension, and no more radial degree of freedom:

(defn elliptical->rect [a b c]
  (fn [[_ [theta phi theta2 phi2] _]]
    (e/up (e/* a (e/sin theta) (e/cos phi))
          (e/* b (e/sin theta) (e/sin phi))
          (e/* c (e/cos theta))
          (e/* a (e/sin theta2) (e/cos phi2))
          (e/* b (e/sin theta2) (e/sin phi2))
          (e/* c (e/cos theta2)))))

;; Next, the Lagrangian:

(defn L-free-particle [m]
  (fn [[_ _ v]]
    (e/* (e// 1 2) m (e/square v))))

(defn L-central-triaxial [m a b c]
  (comp (- (L-free-particle m)
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

(clerk/set-viewers! [(d/literal-viewer d/transform-literal)])

(def local (up 't
               (e/up 'theta_1 'phi_1 'theta_2 'phi_2)
               (e/up 'thetadot_1 'phidot_1 'thetadot_2 'phidot_2)))

((L-central-triaxial 'm 'a 'b 'c) local)

;; simpler if we use `'r` for everything:

((L-central-triaxial 'm 'r 'r 'r) local)

;; I'm sure there's some simplification in there for us. But why?
;;
;; Lagrange equations of motion:

(clerk/with-viewer (d/literal-viewer d/transform-literal)
  (let [L (L-central-triaxial 'm 'a 'b 'c)
        theta (e/literal-function 'theta_1)
        phi   (e/literal-function 'phi_1)
        theta2 (e/literal-function 'theta_2)
        phi2   (e/literal-function 'phi_2)]
    (((e/Lagrange-equations L) (e/up theta phi theta2 phi2))
     't)))

;; if we were back in sphere land:

(clerk/with-viewer (d/literal-viewer d/transform-literal)
  (let [L (L-central-triaxial 'm 'r 'r 'r)
        theta (e/literal-function 'theta)
        phi (e/literal-function 'phi)
        theta2 (e/literal-function 'theta_2)
        phi2   (e/literal-function 'phi_2)]
    (((e/Lagrange-equations L) (e/up theta phi theta2 phi2))
     't)))


;; This is fairly horrifying. This really demands animation, as I bet it looks
;; cool, but it's not comprehensible in this form.
;;
;; Lucky us!! Let's do it!

(def physics-viewer
  {:fetch-fn (fn [_ x] x)
   :transform-fn
   (fn [{:keys [degrees-of-freedom] :as m}]
     (-> (update m :L (fn [L]
                        (-> (e/Lagrangian->state-derivative L)
                            (fs/state-fn->body degrees-of-freedom))))
         (update :state->xyz #(fs/state-fn->body % degrees-of-freedom))))

   :render-fn
   '(fn [value]
      (v/html
       (reagent/with-let
         [!ref   (reagent/atom nil)
          !local (reagent/atom
                  (:initial-state value))]
         (when value
           [:div {:id "mathbox"
                  :style {:height "400px" :width "100%"}
                  :ref
                  (fn [el]
                    (when el
                      (mb/sync!
                       el !ref value
                       mb/sine-setup
                       (fn [mathbox]
                         (mb/double-physics-demo mathbox value !local)))))}]))))})

(clerk/with-viewer physics-viewer
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
