^{:nextjournal.clerk/visibility :hide-ns}
(ns polar
  (:refer-clojure
   :exclude [+ - * / = zero? compare
             numerator denominator ref partial])
  (:require [clojure.walk :as w]
            [demo :as d]
            [nextjournal.clerk :as clerk]
            [sicmutils.env :refer :all]
            [sicmutils.expression.compile :as xc]))

;; ## Polar Example

(def polar-viewer
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
                       mb/polar-setup
                       (fn [mathbox]
                         (mb/polar-demo
                          mathbox value)))))}]))))})

(clerk/with-viewer polar-viewer {})
