^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns demo
  (:refer-clojure
   :exclude [+ - * / = zero? compare
             numerator denominator ref partial])
  (:require [clojure.pprint :as pp]
            [nextjournal.clerk :as clerk]
            [nextjournal.clerk.viewer :as viewer]
            [sicmutils.calculus.form-field :as ff]
            [sicmutils.calculus.indexed :as ci]
            [sicmutils.calculus.vector-field :as vf]
            [sicmutils.env :as e :refer [define-coordinates
                                         let-coordinates
                                         with-literal-functions]]
            [sicmutils.expression :as x]
            [sicmutils.value :as v]))

;; ## Hello
;;
;; Let's create an environment:

(e/bootstrap-repl!)

(+ 'x 'x)

;; ##  Custom Viewers

(defn ->pretty-str [expr]
  (let [form (v/freeze expr)]
    (with-out-str
      (pp/pprint form))))

(defn transform-literal [l]
  (let [simple (simplify l)]
    {:simplified     (clerk/code (->pretty-str simple))
     :simplified_TeX (clerk/tex (->TeX simple))
     :original       (clerk/code (->pretty-str l))
     :TeX            (clerk/tex (->TeX l))}))

#_
(transform-literal (+ (square (sin 'x)) (square (cos 'x))))

(def literal-viewer
  {:pred x/literal?
   :fetch-fn viewer/fetch-all
   :transform-fn transform-literal
   :render-fn
   '(fn [x]
      (v/html
       (reagent/with-let [!sel (reagent/atom (key (first x)))]
         [:<>
          (into
           [:div.flex.items-center.font-sans.text-xs.mb-3
            [:span.text-slate-500.mr-2 (str (ff/square 10) ", View as:")]]
           (map (fn [[l _]]
                  [:button.px-3.py-1.font-medium.hover:bg-indigo-50.rounded-full.hover:text-indigo-600.transition
                   {:class (if (= @!sel l) "bg-indigo-100 text-indigo-600" "text-slate-500")
                    :on-click #(reset! !sel l)}
                   l])
                x))
          (get x @!sel)])))})

(clerk/set-viewers! [literal-viewer])

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



;; This is a custom viewer for a cube rendered
;; with [Mathbox](https://gitgud.io/unconed/mathbox). Note that Mathbox isn't
;; bundled with Clerk but we use a component based
;; on [d3-require](https://github.com/d3/d3-require) to load it at runtime.

(def mathbox-cube
  {:fetch-fn (fn [_ x] x)

   :render-fn
   ;; I was trying here to get some state where I could stash the mathbox
   ;; instance, so I could reset it below when the inputs changed.
   ;;
   ;; But it seems that Clerk only reuses the render function if the value
   ;; doesn't change. If it DOES change (the whole point of !ref) then the form
   ;; is re-evaluated and `!ref` is nil again.
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
                       (fn [mathbox]
                         (let [view (-> mathbox
                                        (.set (clj->js
                                               {:scale 720 :focus 1}))
                                        (.cartesian
                                         (clj->js
                                          {:range [[0 1] [0 1] [0 1]]
                                           :scale [1 1 1]})))

                               ;; and make this function for adding
                               ;; a "volume", which is a 3d data grid you
                               ;; can attach things to...
                               add-volume!
                               (fn [id {:keys [width-rez height-rez depth-rez
                                              size
                                              opacity]
                                       :or {width-rez 4 height-rez 4 depth-rez 4
                                            size 30
                                            opacity 1.0}
                                       :as m}
                                   size opacity]
                                 (doto view
                                   (.volume
                                    (clj->js
                                     {:id id
                                      :width width-rez
                                      :height height-rez
                                      :depth depth-rez
                                      :items 1,
                                      :channels 4
                                      :live false
                                      :expr (fn [emit x y z]
                                              (emit x y z opacity))}))

                                   ;; internally a point is added to each
                                   ;; node in the volume.
                                   (.point
                                    (clj->js
                                     {;; The neat trick: use the same data
                                      ;; for position and for color! We
                                      ;; don't actually need to specify the
                                      ;; points source since we just
                                      ;; defined them but it emphasizes
                                      ;; what's going on.
                                      ;;
                                      ;; The w component 1 is ignored as a
                                      ;; position but used as opacity as a
                                      ;; color.
                                      :points (str "#" id)
                                      :colors (str "#" id)
                                      ;; Multiply every color component in [0..1] by 255
                                      :color 0xffffff
                                      :size size}))))]
                           (add-volume! "volume" value))))))}]))))})

;; We can then use  the above viewer using `with-viewer`:
(clerk/with-viewer mathbox-cube
  {:width-rez 2
   :height-rez 3
   :depth-rez 3
   :size 30
   :opacity 1.0})
