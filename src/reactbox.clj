^{:nextjournal.clerk/visibility :hide-ns}
(ns reactbox
  (:refer-clojure
   :exclude [+ - * / = zero? compare
             numerator denominator ref partial])
  (:require [nextjournal.clerk :as clerk]
            [sicmutils.env :as e :refer :all]
            [sicmutils.expression.compile :as xc]))

;; ## Hello, SICMUtils!!
;;
;; Is this thing on?

(simplify
 (+ 'x 'x (expt 'y 4) 'x 'x))

;; TODO why is reagent doing a full redreaw??
(def cube-viewer
  {:fetch-fn (fn [_ x] x)
   :render-fn
   '(fn [value]
      (v/html
       (when value
         [mbr/cube value])))})

;; We can then use the above viewer using `with-viewer`:

;; live-changing the axes deletes everything
(clerk/with-viewer cube-viewer
  {:id "volume"
   :width-rez 12
   :height-rez 20
   :depth-rez 22
   :size 8
   :opacity 1.0})

;; ## Function Viewer
;;
;; This namespace demos a viewer for function objects; these are functions that
;; _work_ locally, you can call them in the REPL, but you can also send them
;; over the wire to be rendered in the browser.

;;
;; ### Function Compilation
;;
;; Let's assume that the value we will send to the viewer is a map with the
;; function we want to render stored under an `:f` keyword. How do we get the
;; function over to the browser?
;;
;; We can't serialize it directly. What we _can_ do is use SICMUtil's function
;; compiler in `:source` mode to generate a hygienic function literal.
;;
;; Clojure is built out of its own data structures, so that will serialize
;; nicely.

(defn- fn-transform [m]
  (binding [xc/*mode* :source]
    (update m :f #(xc/compile-fn* % 2))))

;; Let's try it:

(fn-transform
 {:f (fn [x _]
       (+ (square x)
          (cube x)))})

;; Not too pretty. We can reuse `d/->pretty-str` here (and already it's obvious
;; that a better presentation would have made `fn-transform` act on the
;; function, not the FULL map, but whatever):

(clerk/code
 (:f (fn-transform
      {:f (fn [x _]
            (+ (square x)
               (cube x)))})))

(def fn-render-fn
  '(fn [value]
     (v/html
      (when value
        (mbr/function value)))))

;; [[fn-render-fn]] also uses some reagent state internally; this is how it's
;; able to compare current and previous values and decide whether or not to
;; re-render.

;; The final viewer is a clojure map with these two pieces supplied:

(def fn-viewer
  {:fetch-fn (fn [_ x] x)
   :transform-fn fn-transform
   :render-fn fn-render-fn})

;; ### Demo
;;
;; Let's make a function to try!

(defn my-fn [x t]
  (+ (square (sin x))
     (square
      (cos (* t x)))))

;; The function works locally, with numbers or symbols:

[(my-fn 1 2) (my-fn 'x 't)]

;; Then we'll call it with our new viewer:

(comment
  (clerk/with-viewer fn-viewer
    {:range [[-6 10] [-1 1] [-1 1]]
     :scale [6 1 1]
     :samples 256
     :f my-fn}))
