(ns demo.mathbox-react
  (:require ["mathbox-react" :as MB]
            [reagent.core :as r]
            [sicmutils.expression.compile :as xc]
            ["three/examples/jsm/controls/OrbitControls.js"
             :as OrbitControls]))

;; OKAY, let's see if we can get this thing going with a basic demo!

(def orbit OrbitControls/OrbitControls)

(def init-cam [2.5 1 2.5])

(def Mathbox (r/adapt-react-class MB/Mathbox))
(def Cartesian (r/adapt-react-class MB/Cartesian))
(def Axis (r/adapt-react-class MB/Axis))
(def Grid (r/adapt-react-class MB/Grid))
(def Volume (r/adapt-react-class MB/Volume))
(def Interval (r/adapt-react-class MB/Interval))
(def Point (r/adapt-react-class MB/Point))
(def Line (r/adapt-react-class MB/Line))

(def options
  {:plugins ["core" "controls" "cursor"]
   :controls {:klass orbit}
   :camera {}})

(defn cube
  [id {:keys [width-rez height-rez depth-rez size opacity]}]
  [(r/adapt-react-class MB/Mathbox)
   {:options {:plugins ["core" "controls" "cursor"]
              :controls {:klass orbit}
              :camera {}}
    :style {:height "400px" :width "100%"}
    :initialCameraPosition init-cam}
   [(r/adapt-react-class MB/Cartesian)
    {}
    [(r/adapt-react-class MB/Volume)
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
     [(r/adapt-react-class MB/Point)
      {:points (str "#" id)
       :colors (str "#" id)
       :color 0xffffff
       :size size}]]]])

(defn function [{:keys [range scale samples f]
                 :or {samples 256
                      range [[-6 10] [-1 1] [-1 1]]
                      scale [6 1 1]}}]
  (let [f' (xc/sci-eval f)]
    [Mathbox
     {:options
      {:plugins ["core" "controls" "cursor"]
       :controls {:klass orbit}
       :camera {}}
      :style {:height "400px" :width "100%"}
      :initialCameraPosition [2.3 1 2]}
     [Cartesian
      {:range range
       :scale scale}
      [Axis {:axis 1 :width 3}]
      [Axis {:axis 2 :width 3}]
      [Axis {:axis 3 :width 3}]
      [Interval
       {:width samples
        :items 1
        :channels 2
        :expr (fn [emit x _i time]
                (let [d (f' x time)]
                  (emit x d)))}
       [Line {:color 0x3090ff :width 4}]
       [Point {:color 0x3090ff :size 8}]]]]))
