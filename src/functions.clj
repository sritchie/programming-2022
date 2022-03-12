^{:nextjournal.clerk/visibility :hide-ns}
(ns functions
  (:refer-clojure
   :exclude [+ - * / = zero? compare
             numerator denominator ref partial])
  (:require [clojure.walk :as w]
            [demo :as d]
            [nextjournal.clerk :as clerk]
            [sicmutils.env :refer :all]
            [sicmutils.expression.compile :as xc]))

;; ##  Custom Viewers
;;
;; Okay, I am leaving this one in a place where I have viewers for a single function. Weird that that single line is in there.

#_
"demos to recreate:

- sine wave: file:///Users/sritchie/code/js/mathbox/examples/test/xyzw.html
- axes plus a function: file:///Users/sritchie/code/js/mathbox/examples/test/vertexcolor.html
- surface file:///Users/sritchie/code/js/mathbox/examples/test/surface.html
- projection onto axes file:///Users/sritchie/code/js/mathbox/examples/test/sources.html"

(defn apply-numeric-ops [body]
  (w/postwalk
   (fn [x]
     (if-let
         [op (and (sequential? x)
                  (every?
                   number?
                   (rest x))
                  (@#'xc/compiled-fn-whitelist (first x)))]
       (double (apply op (rest x)))
       x))
   body))

(defn fn->body
  ([f] (fn->body f (@#'xc/retrieve-arity f)))
  ([f n]
   (let [args (repeatedly n #(gensym 'x))
         body (xc/cse-form
               (freeze
                (simplify (apply f args))))
         body (apply-numeric-ops body)
         body (w/postwalk-replace
               {'sin 'Math/sin
                'cos 'Math/cos
                'expt 'Math/pow
                'sqrt 'Math/sqrt
                'abs 'Math/abs
                'log 'Math/log
                'exp 'Math/exp
                'tan 'Math/tan
                'acos 'Math/acos
                'asin 'Math/asin
                'atan 'Math/atan
                'cosh 'Math/cosh
                'sinh 'Math/sinh
                'tanh 'Math/tanh
                'floor 'Math/floor
                'ceiling 'Math/ceil
                ;; TODO experiment to see if I can symbolically replace these in
                ;; the FULL compiler, that would be faster.
                }
               body)]
     `(fn [~@args] ~body))))

(def fn-viewer
  {:fetch-fn (fn [_ x] x)
   :render-fn
   '(fn [value]
      (v/html
       (reagent/with-let [!ref (reagent/atom nil)]
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
                         (mb/sine-demo mathbox value)))))}]))))})

(def compound-fn-viewer
  (d/literal-viewer
   (fn [{:keys [f args]}]
     {:TeX     (clerk/tex (->TeX (simplify (apply f args))))
      :mathbox (clerk/with-viewer fn-viewer
                 {:range [[-6 6] [-1 1] [-1 1]]
                  :scale [6 1 1]
                  :samples 256
                  :f (fn->body f)})})))

#_
(clerk/with-viewer compound-fn-viewer
  {:f my-fn
   :args ['x 't]})

(defn ->mathbox [f]
  (clerk/with-viewer fn-viewer
    {:range [[-6 6] [-1 1] [-1 1]]
     :scale [6 1 1]
     :samples 256
     :f (fn->body f)}))

(defn my-fn [x t]
  (+ (square (sin x))
     (square
      (cos (* t x)))))

;; We can then use the above viewer using `with-viewer`:
(->mathbox my-fn)

;; And here's the equation:

(clerk/with-viewer (d/literal-viewer d/transform-literal)
  (my-fn 'x 't))

;; PHEW, okay, let's leave it here for now.
