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
    {:simplified     (clerk/code (->pretty-str simple))
     :simplified_TeX (clerk/tex (->TeX simple))
     :original       (clerk/code (->pretty-str l))
     :TeX            (clerk/tex (->TeX l))}))

(transform-literal
 (+ (square (sin 'x)) (square (cos 'x))))

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

;; TODO next: push some of these functions back up into the viewer. Then get
;; another demo going, one that can actually show some physical system evolving.

;;
;; demos to recreate:
;;
;; - sine wave: file:///Users/sritchie/code/js/mathbox/examples/test/xyzw.html
;; - axes plus a function: file:///Users/sritchie/code/js/mathbox/examples/test/vertexcolor.html
;; - surface file:///Users/sritchie/code/js/mathbox/examples/test/surface.html
;;
;; - projection onto axes file:///Users/sritchie/code/js/mathbox/examples/test/sources.html
;;
;; - presentation file:///Users/sritchie/code/js/mathbox/examples/test/present2.html
;; - another: file:///Users/sritchie/code/js/mathbox/examples/test/present.html
;;
;; - polar file:///Users/sritchie/code/js/mathbox/examples/test/polar.html
;;
;; - line, make pendulum with this abd dot file:///Users/sritchie/code/js/mathbox/examples/test/line.html
;;
;; - history, I want this for the pendy path :file:///Users/sritchie/code/js/mathbox/examples/test/history.html

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
                         (-> (mb/->cartesian-view mathbox)
                             (mb/add-volume! "volume" value))))))}]))))})

;; We can then use the above viewer using `with-viewer`:

(clerk/with-viewer mathbox-cube
  {:width-rez 10
   :height-rez 10
   :depth-rez 10
   :size 10
   :opacity 1.0})

;; And another one, this one with special state:

^{::clerk/viewer
  {:pred #(when-let [v (get % ::clerk/var-from-def)]
            (and v (instance? clojure.lang.IDeref (deref v))))
   :fetch-fn (fn [_ x] x)
   :transform-fn (fn [{::clerk/keys [var-from-def]}]
                   {:var-name (symbol var-from-def)
                    :value @@var-from-def})
   :render-fn '(fn [{:keys [var-name value]}]
                 (v/html
                  (reagent/with-let [!ref (reagent/atom nil)]
                    (v/html
                     [:div
                      [:div
                       [:span "width: "]
                       [:input
                        {:type :range
                         :value (:width-rez value)
                         :on-change
                         #(v/clerk-eval
                           `(swap! ~var-name assoc :width-rez (Integer/parseInt ~(.. % -target -value))))}]
                       [:span "height: "]
                       [:input
                        {:type :range
                         :value (:height-rez value)
                         :on-change
                         #(v/clerk-eval
                           `(swap! ~var-name assoc :height-rez (Integer/parseInt ~(.. % -target -value))))}]
                       [:span "depth: "]
                       [:input
                        {:type :range
                         :value (:depth-rez value)
                         :on-change
                         #(v/clerk-eval
                           `(swap! ~var-name assoc :depth-rez (Integer/parseInt ~(.. % -target -value))))}]
                       [:span "Size!!!"]
                       [:input
                        {:type :range
                         :value (:size value)
                         :on-change
                         #(v/clerk-eval
                           `(swap! ~var-name assoc
                                   :size (Integer/parseInt ~(.. % -target -value))))}]]
                      [:div {:id "mathbox"
                             :style {:height "400px" :width "100%"}
                             :ref
                             (fn [el]
                               (when el
                                 (mb/sync!
                                  el !ref value
                                  (fn [mathbox]
                                    (-> (mb/->cartesian-view mathbox)
                                        (mb/add-volume! "volume" value))))))}]]))))}}
(defonce box-state
  (atom
   {:width-rez 8,
    :height-rez 5
    :depth-rez 11
    :size 20, :opacity 1.0}))

@box-state
