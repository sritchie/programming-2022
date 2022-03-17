(ns user
  (:require [nextjournal.clerk.config :as clerk-config]
            [nextjournal.clerk :as clerk]
            [sicmutils.env]
            [sicmutils.expression.render :as xr]))

;; Better rendering for slides.
(alter-var-root
 #'xr/*TeX-vertical-down-tuples*
 (constantly true))

(comment
  (swap! clerk-config/!resource->url
         assoc "/js/viewer.js" "http://localhost:9000/out/main.js")

  ;; Activate this line to start the clerk server.
  (clerk/serve!
   {:browse? true :port 7777}))

(comment
  ;; call clerk/show on files to be rendered:
  ;;
  ;; TODO remove!
  (clerk/show! "src/sicmutils/calculus/derivative.cljc")
  (clerk/show! "src/sicmutils/differential.cljc"))
