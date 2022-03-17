^{:nextjournal.clerk/visibility :hide-ns}
(ns polar
  (:require [nextjournal.clerk :as clerk]))

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
                         (mb/polar-demo mathbox value)))))}]))))})

(clerk/with-viewer polar-viewer {:offset 0.7})
