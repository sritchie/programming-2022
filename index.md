```clojure
^{:nextjournal.clerk/visibility #{:hide :hide-ns}}
(ns index
  (:require [clojure.string :as str]
            [nextjournal.clerk :as clerk]))
```

# 🎪 Programming 2022 [on GitHub](https://github.com/sritchie/programming-2022)

```clojure
(clerk/html
 (into
  [:div.md:grid.md:gap-8.md:grid-cols-2.pb-8]
  (map
   (fn [{:keys [path preview title description]}]
     [:a.rounded-lg.shadow-lg.border.border-gray-300.relative.flex.flex-col.hover:border-indigo-600.group.mb-8.md:mb-0
      {:href (clerk/doc-url path)
       :style {:height 300}}
      [:div.flex-auto.overflow-hidden.rounded-t-md.flex.items-center.px-3.py-4
       [:img {:src preview :width "100%" :style {:object-fit "contain"}}]]
      [:div.sans-serif.border-t.border-gray-300.px-4.py-2.group-hover:border-indigo-600
       [:div.font-bold.block.group-hover:text-indigo-600 title]
       [:div.text-xs.text-gray-500.group-hover:text-indigo-600.leading-normal description]]])
   [{:title "Introduction"
     ;; keeping this so that we get something...
     :preview "https://cdn.nextjournal.com/data/Qmb7qfVDvgcfeEQrfcPwD1DFipw8TuyW8Rno33NAJSYDjr?filename=introduction.png&content-type=image/png"
     :path "src/demo.clj"
     :description "The Basics."}])))
```
