^{:nextjournal.clerk/visibility :hide-ns}
(ns reactbox
  (:refer-clojure
   :exclude [+ - * / = zero? compare
             numerator denominator ref partial])
  (:require [nextjournal.clerk :as clerk]
            [sicmutils.env :as e :refer :all]
            [sicmutils.expression.compile :as xc]))

(def cube-viewer
  {:fetch-fn (fn [_ x] x)
   :render-fn
   '(fn [{:keys [id width-rez height-rez depth-rez size opacity]}]
      (v/html
       [box/Mathbox
        {:options {:plugins ["core" "controls" "cursor"]
                   :controls {:klass box/OrbitControls}
                   :camera {}}
         :style {:height "400px" :width "100%"}
         :initialCameraPosition [2.5 1 2.5]
         :ref (fn [box]
                (when box
                  (doto (.-three ^js box)
                    (-> .-controls .-maxDistance (set! 4))
                    (-> .-camera .-position (.set 2.5 1 2.5))
                    (-> .-renderer (.setClearColor (mb/color 0xeeeeee) 1.0)))))}
        [box/Cartesian
         {}
         [box/Volume
          {:id id
           :width width-rez
           :bufferWidth width-rez
           :height height-rez
           :bufferHeight height-rez
           :depth depth-rez
           :bufferDepth depth-rez
           :items 1
           :channels 4
           :live false
           :expr (fn [emit x y z]
                   (emit x y z opacity))}
          [box/Point
           {:points (str "#" id)
            :colors (str "#" id)
            :color 0xffffff
            :size size}]]]]))})

(clerk/with-viewer cube-viewer
  {:id "volume"
   :width-rez 12
   :height-rez 20
   :depth-rez 22
   :size 8
   :opacity 1.0})

;; ## Stateful Attempt

^{::clerk/viewer
  '(fn [_] (defonce cube-state
            (reagent/atom
             {:width-rez 12
              :height-rez 20
              :depth-rez 22
              :size 8
              :opacity 1.0})))}
(do :ignore)

^{::clerk/viewer
  '(fn [_]
     (v/html
      [box/Mathbox
       {:options {:plugins ["core" "controls" "cursor"]
                  :controls {:klass box/OrbitControls}
                  :camera {}}
        :style {:height "400px" :width "100%"}
        :initialCameraPosition [2.5 1 2.5]
        :ref (fn [box]
               (when box
                 (doto (.-three ^js box)
                   (-> .-controls .-maxDistance (set! 4))
                   (-> .-camera .-position (.set 2.5 1 2.5))
                   (-> .-renderer (.setClearColor (mb/color 0xeeeeee) 1.0)))))}
       [box/Cartesian
        {}
        (let [{:keys [width-rez height-rez depth-rez size opacity]} ]
          [box/Volume
           {:id "volume"
            :width (:width-rez @cube-state)
            :bufferWidth (:width-rez @cube-state)
            :height (:height-rez @cube-state)
            :bufferHeight (:height-rez @cube-state)
            :depth (:depth-rez @cube-state)
            :bufferDepth (:depth-rez @cube-state)
            :items 1
            :channels 4
            :live false
            :expr (fn [emit x y z]
                    (emit x y z (:opacity @cube-state)))}
           [box/Point
            {:points "#volume"
             :colors "#volume"
             :color 0xffffff
             :size (:size @cube-state)}]])]]))}
(do :also-ignored)

^{::clerk/viewer
  {:fetch-fn (fn [_ x] x)
   :render-fn '#(reset! cube-state %)}}
{:width-rez 1
 :height-rez 20
 :depth-rez 22
 :size 8
 :opacity 1.0}


;; ignore this for now!
(comment
  (def cube-viewer
    {:fetch-fn (fn [_ x] x)
     :render-fn
     '(fn [value]
        (v/html
         (when value
           [mbr/cube value])))})

  (clerk/with-viewer cube-viewer
    {:id "volume"
     :width-rez 12
     :height-rez 20
     :depth-rez 22
     :size 8
     :opacity 1.0}))

;; ## Function Viewer

(comment
  (defn- fn-transform [m]
    (binding [xc/*mode* :source]
      (update m :f #(xc/compile-fn* % 2))))

  (def fn-render-fn
    '(fn [value]
       (v/html
        (when value
          (mbr/function value)))))

  (def fn-viewer
    {:fetch-fn (fn [_ x] x)
     :transform-fn fn-transform
     :render-fn fn-render-fn})

  (defn my-fn [x t]
    (+ (square (sin x))
       (square
        (cos (* t x)))))

  (clerk/with-viewer fn-viewer
    {:range [[-6 10] [-1 1] [-1 1]]
     :scale [6 1 1]
     :samples 256
     :f my-fn}))
