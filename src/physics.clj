^{:nextjournal.clerk/visibility :hide-ns}
(ns physics
  (:refer-clojure
   :exclude [+ - * / = zero? compare
             numerator denominator ref partial])
  (:require [demo :as d]
            [functions :as fs]
            [nextjournal.clerk :as clerk]
            [sicmutils.env :as e :refer :all]))

;; ## Physics Example

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
  (fn [[_ [theta phi] _]]
    (e/up (e/* a (e/sin theta) (e/cos phi))
          (e/* b (e/sin theta) (e/sin phi))
          (e/* c (e/cos theta)))))

;; Next, the Lagrangian:

(defn L-free-particle [m]
  (fn [[_ _ v]]
    (e/* (e// 1 2) m (e/square v))))

(defn L-central-triaxial [m a b c]
  (comp (- (L-free-particle m)
           (fn [[_ [_ _ z]]]
             (* 9.8 m z)))
        (e/F->C (elliptical->rect a b c))))

;; Final Lagrangian:

(clerk/set-viewers! [(d/literal-viewer d/transform-literal)])

(let [local (up 't
                (e/up 'theta 'phi)
                (e/up 'thetadot 'phidot))]
  ((L-central-triaxial 'm 'a 'b 'c) local))

;; simpler if we use `'r` for everything:

(let [local (up 't
                (e/up 'theta 'phi)
                (e/up 'thetadot 'phidot))]
  ((L-central-triaxial 'm 'r 'r 'r) local))

;; I'm sure there's some simplification in there for us. But why?
;;
;; Lagrange equations of motion:

(clerk/with-viewer (d/literal-viewer d/transform-literal)
  (let [L (L-central-triaxial 'm 'a 'b 'c)
        theta (e/literal-function 'theta)
        phi (e/literal-function 'phi)]
    (((e/Lagrange-equations L) (e/up theta phi))
     't)))

;; if we were back in sphere land:

(clerk/with-viewer (d/literal-viewer d/transform-literal)
  (let [L (L-central-triaxial 'm 'r 'r 'r)
        theta (e/literal-function 'theta)
        phi (e/literal-function 'phi)]
    (((e/Lagrange-equations L) (e/up theta phi))
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
                         (mb/physics-demo mathbox value !local)))))}]))))})

(clerk/with-viewer physics-viewer
  (let [m 10, a 2, b 1, c 1]
    {:degrees-of-freedom 2
     :state->xyz (elliptical->rect a b c)

     :L          (L-central-triaxial m a b c)

     :initial-state [0 [0.1 0.1] [0 0 ]]
     :ellipse {:a a :b b :c c}
     :cartesian
     {:range [[-10, 10]
              [-10, 10]
              [-10, 10]]
      :scale [3 3 3]}}))
