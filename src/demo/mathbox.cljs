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
  [el]
  (let [box (build-mathbox
             {:plugins  ["core" "controls" "cursor"]
              :controls {:klass orbit}
              :element el
              :camera  {}})]
    (setup-scene box)
    (set! (.-mathboxView ^js el) box)
    box))

(defn sync! [el !state new f]
  (let [box (or (.-mathboxView ^js el)
                (initialize! el))]
    (when-not (= @!state new)
      (reset! !state new)
      (.remove box "*")
      (f box))))

#_
(let [box (mb/build-mathbox
           {:plugins  ["core" "controls" "cursor"]
            :controls {:klass mb/orbit}
            :element el
            :camera  {}})]
  (.log js/console "building!" )
  (mb/setup-scene box)
  (set! (.-mathboxView el) box))

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

#_
(comment
  (add-volume! "volume2" 4 30 1.0)
  (add-volume! "volume3" 5 80 1.0))
