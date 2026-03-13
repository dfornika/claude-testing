(ns app.core)

;; ---------------------------------------------------------------------------
;; Palettes — each entry is [hue-min hue-max saturation brightness name]
;; ---------------------------------------------------------------------------

(def palettes
  [{:name "Aurora"    :h1 160 :h2 280 :s 80 :b 90}
   {:name "Ember"     :h1 0   :h2 45  :s 90 :b 95}
   {:name "Ocean"     :h1 180 :h2 240 :s 85 :b 85}
   {:name "Neon"      :h1 280 :h2 340 :s 95 :b 100}
   {:name "Mono"      :h1 0   :h2 0   :s 0  :b 95}])

;; ---------------------------------------------------------------------------
;; Particle helpers
;; ---------------------------------------------------------------------------

(defn make-particle [p w h]
  {:x     (.random p w)
   :y     (.random p h)
   :vx    0
   :vy    0
   :hue   (.random p 360)})

(defn wrap [v limit]
  (cond (< v 0) limit (> v limit) 0 :else v))

(defn update-particle [p particle field-scale z w h]
  (let [{:keys [x y vx vy hue]} particle
        nx    (* x field-scale)
        ny    (* y field-scale)
        angle (* (.noise p nx ny z) js/Math.PI 4)
        nvx   (+ (* vx 0.9) (* (.cos p angle) 1.5))
        nvy   (+ (* vy 0.9) (* (.sin p angle) 1.5))]
    {:x   (wrap (+ x nvx) w)
     :y   (wrap (+ y nvy) h)
     :vx  nvx
     :vy  nvy
     :hue (mod (+ hue 0.3) 360)}))

;; ---------------------------------------------------------------------------
;; Sketch factory
;; ---------------------------------------------------------------------------

(defn make-sketch []
  (fn [p]
    (let [state (atom {:z          0
                       :palette-idx 0
                       :particles  []})]

      (set! (.-setup p)
            (fn []
              (.createCanvas p (.-windowWidth js/window) (.-windowHeight js/window))
              (.colorMode p (.-HSB p) 360 100 100 100)
              (.frameRate p 30)
              (.noStroke p)
              (swap! state assoc :particles
                     (vec (repeatedly 150
                                      #(make-particle p
                                                      (.-width p)
                                                      (.-height p)))))))

      (set! (.-draw p)
            (fn []
              (let [{:keys [z palette-idx particles]} @state
                    {:keys [h1 h2 s b]}              (nth palettes palette-idx)
                    w  (.-width p)
                    h  (.-height p)
                    scale 0.0035]

                ;; Fade trail: semi-transparent black rect over full canvas
                (.fill p 0 0 0 8)
                (.rect p 0 0 w h)

                ;; Draw and update each particle
                (let [new-particles
                      (mapv (fn [particle]
                              (let [updated (update-particle p particle scale z w h)
                                    hue     (if (= h1 h2)
                                              0
                                              (+ h1 (* (/ (mod (:hue updated) 360) 360)
                                                       (- h2 h1))))]
                                (.fill p hue s b 70)
                                (.circle p (:x updated) (:y updated) 2.5)
                                updated))
                            particles)]

                  (swap! state assoc
                         :particles new-particles
                         :z (+ z 0.004))))))

      (set! (.-mousePressed p)
            (fn []
              (swap! state (fn [{:keys [palette-idx z] :as st}]
                             (let [next-idx (mod (inc palette-idx) (count palettes))]
                               (set! (.-innerHTML
                                       (.getElementById js/document "palette-name"))
                                     (:name (nth palettes next-idx)))
                               (assoc st
                                      :palette-idx next-idx
                                      :z (+ z 50)))))
              false))   ;; prevent default / scroll

      (set! (.-touchStarted p)
            (.-mousePressed p))

      (set! (.-windowResized p)
            (fn []
              (.resizeCanvas p (.-windowWidth js/window) (.-windowHeight js/window)))))))

;; ---------------------------------------------------------------------------
;; Entry point
;; ---------------------------------------------------------------------------

(defn ^:export init []
  (js/p5. (make-sketch)))
