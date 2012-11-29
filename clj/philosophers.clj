(def philosophers (vec (map ref (repeat 5 :waiting))))

(def forks (cycle (map ref (repeat 5 nil))))

(defn forks-get [i]
  [(nth forks i) (nth forks (inc i))])

(defn forks-set [i v]
  (dosync (doseq [f (forks-get i)] (ref-set f v))))

(defn forks-put [i]
  (dosync
    (assert (every? #(= % i) (mapv deref (forks-get i))))
    (forks-set i nil)))

(defn forks-free? [i]
  (dosync (every? nil? (map deref (forks-get i)))))

(defn forks-pick? [i]
  (dosync
    (when (forks-free? i)
      (forks-set i i) true)))

(defn eat-and-think [i eat-time think-time retry-time]
  (let [p (philosophers i)]
    (if (forks-pick? i)
      (do
        (dosync
          (ref-set p :eating))
        (Thread/sleep eat-time)
        (dosync
          (forks-put i)
          (ref-set p :thinking))
        (Thread/sleep think-time)
        (dosync
          (ref-set p :waiting)))
      (Thread/sleep retry-time))))


(defn print-status [ts]
  (dosync
    (println
      (format "%4dms" ts)
      (apply str (for [p philosophers]
        ({:eating "E" :thinking "." :waiting "x"} @p))))))
    ;(println "      " (map deref (take 5 forks)))))

(let [interval 50]
  (future (loop [ts 0]
            (print-status ts)
            (Thread/sleep interval)
            (recur (+ ts interval)))))


(doseq [i (range 5)]
  (future (loop []
            ;(eat-and-think i (rand-int 100) (rand-int 100) 10)
            (eat-and-think i 100 100 10)
            (recur))))
