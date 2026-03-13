(ns app.core)

(defn mount-app []
  (let [app-element (.getElementById js/document "app")]
    (set! (.-innerHTML app-element)
          "<div class='container'>
             <h1>Hello ClojureScript!</h1>
             <p>Welcome to your minimal ClojureScript app.</p>
             <button id='click-btn'>Click me!</button>
             <p id='counter'>Clicks: 0</p>
           </div>")))

(defn add-click-handler []
  (let [btn (.getElementById js/document "click-btn")
        counter-element (.getElementById js/document "counter")
        clicks (atom 0)]
    (.addEventListener btn "click"
                       (fn []
                         (swap! clicks inc)
                         (set! (.-innerHTML counter-element)
                               (str "Clicks: " @clicks))))))

(defn ^:export init []
  (mount-app)
  (add-click-handler))
