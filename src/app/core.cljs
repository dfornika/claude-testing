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
;; Gyroscope — updated by DeviceOrientationEvent, consumed in update-particle
;; ---------------------------------------------------------------------------

(defonce gyro (atom {:gamma 0 :beta 0}))
(defonce gyro-started (atom false))

(defn start-gyro []
  (when-not @gyro-started
    (reset! gyro-started true)
    (let [listen (fn []
                   (.addEventListener js/window "deviceorientation"
                     (fn [e]
                       (when (.-gamma e)
                         (reset! gyro {:gamma (.-gamma e)
                                       :beta  (.-beta e)})))))]
      ;; iOS 13+ requires an explicit permission request from a user gesture
      (if (and (.-DeviceOrientationEvent js/window)
               (.-requestPermission (.-DeviceOrientationEvent js/window)))
        (-> (.requestPermission (.-DeviceOrientationEvent js/window))
            (.then (fn [result]
                     (when (= result "granted") (listen)))))
        ;; Android and older iOS — works immediately
        (listen)))))

;; ---------------------------------------------------------------------------
;; Particle helpers
;; ---------------------------------------------------------------------------

(defn make-particle [p w h]
  {:x   (.random p w)
   :y   (.random p h)
   :vx  0
   :vy  0
   :hue (.random p 360)})

(defn wrap [v limit]
  (cond (< v 0) limit (> v limit) 0 :else v))

(defn update-particle [p particle field-scale z w h]
  (let [{:keys [x y vx vy hue]} particle
        nx    (* x field-scale)
        ny    (* y field-scale)
        angle (* (.noise p nx ny z) js/Math.PI 4)
        ;; Gyro bias: gamma = left/right tilt, beta ≈ 90 when phone upright
        {:keys [gamma beta]} @gyro
        gx    (* (/ gamma 90) 0.4)
        gy    (* (/ (- beta 90) 90) 0.4)
        nvx   (+ (* vx 0.9) (* (.cos p angle) 1.5) gx)
        nvy   (+ (* vy 0.9) (* (.sin p angle) 1.5) gy)]
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
              (.createCanvas p (.-windowWidth p) (.-windowHeight p))
              (.colorMode p (.-HSB p) 360 100 100 100)
              (.background p 0 0 0)
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

                ;; Fade trail
                (.fill p 0 0 0 8)
                (.rect p 0 0 w h)

                ;; Draw and update each particle with layered glow
                (let [new-particles
                      (mapv (fn [particle]
                              (let [updated (update-particle p particle scale z w h)
                                    hue     (if (= h1 h2)
                                              0
                                              (+ h1 (* (/ (mod (:hue updated) 360) 360)
                                                       (- h2 h1))))]
                                ;; Outer smoke halo — large, very transparent
                                (.fill p hue s b 12)
                                (.circle p (:x updated) (:y updated) 10)
                                ;; Bright inner core
                                (.fill p hue s (min 100 (+ b 5)) 85)
                                (.circle p (:x updated) (:y updated) 3)
                                updated))
                            particles)]

                  (swap! state assoc
                         :particles new-particles
                         :z (+ z 0.004))))))

      (set! (.-mousePressed p)
            (fn []
              ;; Request gyro permission on first tap (iOS 13+)
              (start-gyro)
              (swap! state (fn [{:keys [palette-idx z] :as st}]
                             (let [next-idx (mod (inc palette-idx) (count palettes))]
                               (set! (.-innerHTML
                                       (.getElementById js/document "palette-name"))
                                     (:name (nth palettes next-idx)))
                               (assoc st
                                      :palette-idx next-idx
                                      :z (+ z 50)))))
              false))

      (set! (.-touchStarted p)
            (.-mousePressed p))

      (set! (.-windowResized p)
            (fn []
              (.resizeCanvas p (.-windowWidth p) (.-windowHeight p)))))))

;; ---------------------------------------------------------------------------
;; Entry point
;; ---------------------------------------------------------------------------

(defn ^:export init []
  (start-gyro)   ;; starts immediately on Android; iOS permission deferred to first tap
  (js/p5. (make-sketch)))
