(ns demo.mathbox
  (:require ["mathbox" :as MathBox]
            ["three" :as THREE]
            ["three/examples/jsm/controls/OrbitControls.js"
             :as OrbitControls]))

(defn build-mathbox [opts]
  (MathBox/mathBox
   (clj->js opts)))

(defn color [x]
  (THREE/Color. x))

(def orbit OrbitControls/OrbitControls)

(defn default-mathbox
  "Still not sure this makes any sense, but good stuff."
  [el]
  (build-mathbox
   {:plugins  ["core" "controls" "cursor"]
    :controls {:klass orbit}
    :element el
    :camera  {}}))

(defn setup-scene [box]
  (doto (.-three ^js box)
    (-> .-controls .-maxDistance (set! 4))
    (-> .-camera .-position (.set 2.5 1 2.5))
    (-> .-renderer (.setClearColor (color 0xeeeeee) 1.0)))
  box)

(defn initialize!
  "Used."
  [el setup-fn]
  (let [box (build-mathbox
             {:plugins  ["core" "controls" "cursor"]
              :controls {:klass orbit}
              :element el})]
    (setup-fn box)
    (set! (.-mathboxView ^js el) box)
    box))

(defn sync! [el !state new setup-fn f]
  (let [box (or (.-mathboxView ^js el)
                (initialize! el setup-fn))]
    (when-not (= @!state new)
      (reset! !state new)
      (.remove box "*")
      (f box))))

(defn ->cartesian-view
  "TODO allow options."
  [box]
  (-> box
      (.set (clj->js {:scale 720 :focus 1}))
      (.cartesian
       (clj->js
        {:range [[0 1] [0 1] [0 1]]
         :scale [1 1 1]}))))

(defn add-volume!
  "and make this function for adding
  a volume which is a 3d data grid you
  can attach things to..."
  [view id {:keys [width-rez height-rez depth-rez
                   size
                   opacity]
            :or {width-rez 4 height-rez 4 depth-rez 4
                 size 30
                 opacity 1.0}}]
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
       :size size}))))

;; Sine Wave (generic fns?)

(defn sine-setup [box]
  (doto (.-three ^js box)
    (-> .-camera .-position (.set 2.3 1 2))
    (-> .-renderer (.setClearColor (color 0xffffff) 1.0))))

(defn sine-demo [box {:keys [range scale samples f]}]
  (let [f' (eval f)]
    (-> box
        (.cartesian
         (clj->js
          {:range range
           :scale scale}))
        (.axis #js {:axis 1 :width 3})
        (.axis #js {:axis 2 :width 3})
        (.axis #js {:axis 3 :width 3})
        #_(.grid #js {:width 2 :divideX 20 :divideY 10})
        (.interval
         (clj->js
          {:width samples
           ;; one tick for each function.
           :items 1
           ;; Here, x is the sampled X coordinate,
           ;; i is the array index (0-63), and
           ;; t is clock time in seconds, starting from 0.
           ;;
           ;; The use of emit is similar to returning a value. It is used to allow
           ;; multiple values to be emitted very efficiently.
           :expr (fn [emit x _i time]
                   (let [d (f' x time)]
                     ;; so emit once for each function we want to log.
                     (emit x d)))

           ;; 2 channels == x, y values.
           :channels 2}))
        (.line #js {:color 0x3090ff :width 4})
        (.point #js {:color 0x3090ff :size 8}))))
