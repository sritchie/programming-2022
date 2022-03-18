^{:nextjournal.clerk/visibility :hide-ns}
(ns demo
  (:refer-clojure
   :exclude [+ - * / = zero? compare
             numerator denominator ref partial])
  (:require [clojure.pprint :as pp]
            [nextjournal.clerk :as clerk]
            [nextjournal.clerk.viewer :as viewer]
            [sicmutils.env :as e]
            [sicmutils.expression :as x]
            [sicmutils.value :as v]))

;; ## Hello
;;
;; Let's create an environment:

(e/bootstrap-repl!)

(+ 'x 'x 'x 'x 'x)

;; ##  Custom Viewers

(defn ->pretty-str [expr]
  (let [form (v/freeze expr)]
    (with-out-str
      (pp/pprint form))))

(defn transform-literal [l]
  (let [simple (simplify l)]
    {:simplified_TeX (clerk/tex (->TeX simple))
     :simplified     (clerk/code (->pretty-str simple))
     :TeX            (clerk/tex (->TeX l))
     :original       (clerk/code (->pretty-str l))}))

(transform-literal
 (+ (square (sin 'x)) (square (cos 'x))))

(defn literal-viewer [xform]
  {:pred x/literal?
   :fetch-fn viewer/fetch-all
   :transform-fn (memoize xform)
   :render-fn
   '(fn [x]
      (v/html
       (reagent/with-let [!sel (reagent/atom (key (first x)))]
         [:<>
          (into
           [:div.flex.items-center.font-sans.text-xs.mb-3
            [:span.text-slate-500.mr-2 "View as:"]]
           (map (fn [[l _]]
                  [:button.px-3.py-1.font-medium.hover:bg-indigo-50.rounded-full.hover:text-indigo-600.transition
                   {:class (if (= @!sel l)
                             "bg-indigo-100 text-indigo-600"
                             "text-slate-500")
                    :on-click #(reset! !sel l)}
                   l])
                x))
          (get x @!sel)])))})

(clerk/set-viewers! [(literal-viewer transform-literal)])

;; ## Woohoo
;;
;; Now we have a multiviewer, SO nice.

(+ (square (sin 'x))
   (square (cos 'x)))

;; How about the field equations?

(/ (+ (* 'A 'C 'gMR (expt (sin 'theta) 2) (cos 'theta))
      (* (/ 1 2) 'A (expt 'p_psi 2) (expt (sin 'theta) 2))
      (* (/ 1 2) 'C (expt 'p_psi 2) (expt (cos 'theta) 2))
      (* (/ 1 2) 'C (expt 'p_theta 2) (expt (sin 'theta) 2))
      (* -1 'C 'p_phi 'p_psi (cos 'theta))
      (* (/ 1 2) 'C (expt 'p_phi 2)))
   (* 'A 'C (expt (sin 'theta) 2)))
