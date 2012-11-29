;; Definiere einen Vektor von 6 Referenzen
;; mit Initialwert 0
(def accounts (mapv ref (repeat 6 0)))

;; Funktion, die eine Transaktion ausführt.
;; Es wird 10-110ms gewartet, dann wird die
;; Referenz 'source' dekrementiert. Nach genau
;; 100ms wird die Referenz 'target' inkrementiert.
(defn transfer [source target]
  (dosync
    (Thread/sleep (+ 10 (rand-int 100)))
    (commute source dec)
    (Thread/sleep 100)
    (commute target inc)))

;; Gibt alle 50ms die einzelnen Kontostände
;; und die Summe aus. Es wird erwartet, dass die
;; Summe zu jedem Zeitpunkt 0 ist.
(defn print-status-loop []
  (doseq [i (range)]
    (dosync
      (let [balances (map deref accounts)]
        (println
          (format "[%3d]" i) "sum" balances "=" (apply + balances))))
    (Thread/sleep 50)
    (recur (inc i))))

(future (print-status-loop))

;; Fuehre 100 parallele Transfer-Transaktionen
;; zwischen zwei zufällig gewählten Referenzen aus
(doseq [i (range 100)]
  (future (transfer (rand-nth accounts) (rand-nth accounts))))
