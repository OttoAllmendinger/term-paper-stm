(def customer (ref nil))

(def waiting-room (ref ()))

(def waiting-room-size 10)

(def barber-check)

(defn barber-take-customer [i]
  (dosync (ref-set customer i))
  (when-not (nil? i)
    (future
      (Thread/sleep 100)
      (barber-check))))

(defn barber-check []
  (dosync
    (barber-take-customer (last @waiting-room))
    (alter waiting-room drop-last)))


(defn salon-enter [i]
  (dosync
    (if (nil? @customer)
      (barber-take-customer i)
      (when-not (> (count @waiting-room) waiting-room-size)
        (alter waiting-room conj i)))))

(defn customer-loop [prefix]
  (doseq [i (range)]
    (salon-enter (str prefix i))
    (Thread/sleep (rand-int 500))))

(defn status-loop []
  (doseq [i (range)]
    (dosync
      (println "customer" @customer "waiting-room" @waiting-room))
    (Thread/sleep 40)))

(future (status-loop))
(future (customer-loop "a"))
(future (customer-loop "b"))
(future (customer-loop "c"))
