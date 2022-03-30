(ns demo.viewers
  (:require [demo.mathbox]
            [demo.mathbox-react]
            [nextjournal.clerk.sci-viewer :as sv]
            [sci.core :as sci]))

;; Here is the existing context:
;; https://github.com/nextjournal/clerk/blob/d08c26043efe19a92fe33dd9eb4499e304e4cff7/src/nextjournal/clerk/sci_viewer.cljs#L1013-L1023

(sci/require-cljs-analyzer-api)

(swap! sv/!sci-ctx
       sci/merge-opts
       {:namespaces
        {'demo.mathbox
         (sci/copy-ns demo.mathbox (sci/create-ns 'demo.mathbox))

         'demo.mathbox-react
         (sci/copy-ns demo.mathbox-react (sci/create-ns 'demo.mathbox-react))}
        :classes {'Math js/Math}
        :aliases {'mb 'demo.mathbox
                  'mbr 'demo.mathbox-react}})
