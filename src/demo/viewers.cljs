(ns demo.viewers
  (:require [demo.mathbox]
            [nextjournal.clerk.sci-viewer :as sv]
            [sci.core :as sci]))

;; Here is the existing context:
;; https://github.com/nextjournal/clerk/blob/d08c26043efe19a92fe33dd9eb4499e304e4cff7/src/nextjournal/clerk/sci_viewer.cljs#L1013-L1023

(swap! sv/!sci-ctx
       sci/merge-opts
       {:namespaces
        {'demo.mathbox
         {'build-mathbox demo.mathbox/build-mathbox
          'color demo.mathbox/color
          'orbit demo.mathbox/orbit
          'setup-scene demo.mathbox/setup-scene
          'initialize! demo.mathbox/initialize!
          'sync! demo.mathbox/sync!
          '->cartesian-view demo.mathbox/->cartesian-view
          'add-volume! demo.mathbox/add-volume!

          'sine-setup demo.mathbox/sine-setup
          'sine-demo demo.mathbox/sine-demo

          'polar-setup demo.mathbox/polar-setup
          'polar-demo demo.mathbox/polar-demo

          'physics-demo demo.mathbox/physics-demo
          'double-physics-demo demo.mathbox/double-physics-demo}}
        :classes {'Math js/Math}
        :aliases {'mb 'demo.mathbox}})
