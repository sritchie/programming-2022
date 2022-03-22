(ns demo.viewers
  (:require [demo.mathbox]
            [nextjournal.clerk.sci-viewer :as sv]
            [sci.core :as sci]))

;; Here is the [original
;; context](https://github.com/nextjournal/clerk/blob/d08c26043efe19a92fe33dd9eb4499e304e4cff7/src/nextjournal/clerk/sci_viewer.cljs#L1013-L1023).
;;

(swap! sv/!sci-ctx
       sci/merge-opts
       {:namespaces
        {'demo.mathbox
         (sci/copy-ns demo.mathbox (sci/create-ns 'demo.mathbox))}
        :classes {'Math js/Math}
        :aliases {'mb 'demo.mathbox}})
