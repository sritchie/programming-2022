^{:nextjournal.clerk/visibility :hide-ns}
(ns cube-controls
  (:require [nextjournal.clerk :as clerk]))

;; ##  Custom Viewers

;; This is a custom viewer for a cube rendered with [Mathbox](https://gitgud.io/unconed/mathbox).

(def mathbox-cube
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
                       mb/setup-scene
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
                 (letfn [(elem [s k]
                           [:div
                            [:span s]
                            [:input
                             {:type :range
                              :value (k value)
                              :on-change
                              #(v/clerk-eval
                                `(swap! ~var-name assoc ~k (Integer/parseInt
                                                            ~(.. % -target -value))))}]])]
                   (v/html
                    (reagent/with-let [!ref (reagent/atom nil)]
                      (v/html
                       [:div
                        [:div
                         (elem "width: " :width-rez)
                         (elem "height: " :height-rez)
                         (elem "depth: " :depth-rez)
                         (elem "size: " :size)
                         [:div
                          [:span "Opacity: " ]
                          [:input
                           {:type :range
                            :value (* 100.0 (:opacity value))
                            :on-change
                            #(v/clerk-eval
                              `(swap! ~var-name assoc
                                      :opacity
                                      (-> (Double/parseDouble
                                           ~(.. % -target -value))
                                          (/ 100.0))))}]]]
                        [:div {:id "mathbox"
                               :style {:height "400px" :width "100%"}
                               :ref
                               (fn [el]
                                 (when el
                                   (mb/sync!
                                    el !ref value
                                    mb/setup-scene
                                    (fn [mathbox]
                                      (-> (mb/->cartesian-view mathbox)
                                          (mb/add-volume! "volume" value))))))}]])))))}}
(defonce box-state
  (atom
   {:width-rez 8,
    :height-rez 5
    :depth-rez 11
    :size 20
    :opacity 1.0}))

@box-state
