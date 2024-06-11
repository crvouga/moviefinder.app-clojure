(ns moviefinder-app.view.icon)

(defn home []
  [:svg {:xmlns "http://www.w3.org/2000/svg" :viewbox "0 0 24 24" :fill "currentColor" :class "size-6"} 
   [:path {:d "M11.47 3.841a.75.75 0 0 1 1.06 0l8.69 8.69a.75.75 0 1 0 1.06-1.061l-8.689-8.69a2.25 2.25 0 0 0-3.182 0l-8.69 8.69a.75.75 0 1 0 1.061 1.06l8.69-8.689Z"}]
   [:path {:d "m12 5.432 8.159 8.159c.03.03.06.058.091.086v6.198c0 1.035-.84 1.875-1.875 1.875H15a.75.75 0 0 1-.75-.75v-4.5a.75.75 0 0 0-.75-.75h-3a.75.75 0 0 0-.75.75V21a.75.75 0 0 1-.75.75H5.625a1.875 1.875 0 0 1-1.875-1.875v-6.198a2.29 2.29 0 0 0 .091-.086L12 5.432Z"}]])


(defn user-circle []
  [:svg {:xmlns "http://www.w3.org/2000/svg" :viewbox "0 0 24 24" :fill "currentColor" :class "size-6"}
   [:path {:fill-rule "evenodd" :d "M18.685 19.097A9.723 9.723 0 0 0 21.75 12c0-5.385-4.365-9.75-9.75-9.75S2.25 6.615 2.25 12a9.723 9.723 0 0 0 3.065 7.097A9.716 9.716 0 0 0 12 21.75a9.716 9.716 0 0 0 6.685-2.653Zm-12.54-1.285A7.486 7.486 0 0 1 12 15a7.486 7.486 0 0 1 5.855 2.812A8.224 8.224 0 0 1 12 20.25a8.224 8.224 0 0 1-5.855-2.438ZM15.75 9a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0Z" :clip-rule "evenodd"}]])


(defn arrow-left []
  [:svg {:xmlns "http://www.w3.org/2000/svg" :viewbox "0 0 24 24" :fill "currentColor" :class "size-6"}
   [:path {:fill-rule "evenodd" :d "M11.03 3.97a.75.75 0 0 1 0 1.06l-6.22 6.22H21a.75.75 0 0 1 0 1.5H4.81l6.22 6.22a.75.75 0 1 1-1.06 1.06l-7.5-7.5a.75.75 0 0 1 0-1.06l7.5-7.5a.75.75 0 0 1 1.06 0Z" :clip-rule "evenodd"}]])

(defn door 
  ([]
   (door {}))
  ([props]
   [:svg (merge {:xmlns "http://www.w3.org/2000/svg" :class "size-6" :fill "currentColor" :viewbox "0 0 576 512"} props)
    [:path {:d "M320 32c0-9.9-4.5-19.2-12.3-25.2S289.8-1.4 280.2 1l-179.9 45C79 51.3 64 70.5 64 92.5V448H32c-17.7 0-32 14.3-32 32s14.3 32 32 32H96 288h32V480 32zM256 256c0 17.7-10.7 32-24 32s-24-14.3-24-32s10.7-32 24-32s24 14.3 24 32zm96-128h96V480c0 17.7 14.3 32 32 32h64c17.7 0 32-14.3 32-32s-14.3-32-32-32H512V128c0-35.3-28.7-64-64-64H352v64z"}]]))

(defn checkmark-circle
  ([]
   (checkmark-circle {}))
  ([props]
   [:svg (merge {:xmlns "http://www.w3.org/2000/svg", :viewbox "0 0 24 24", :fill "currentColor", :class "size-6"} props)
    [:path {:fill-rule "evenodd", :d "M2.25 12c0-5.385 4.365-9.75 9.75-9.75s9.75 4.365 9.75 9.75-4.365 9.75-9.75 9.75S2.25 17.385 2.25 12Zm13.36-1.814a.75.75 0 1 0-1.22-.872l-3.236 4.53L9.53 12.22a.75.75 0 0 0-1.06 1.06l2.25 2.25a.75.75 0 0 0 1.14-.094l3.75-5.25Z", :clip-rule "evenodd"}]]))

(defn exclaimation-circle 
  ([]
   (exclaimation-circle {}))
  ([props]
   [:svg (merge {:xmlns "http://www.w3.org/2000/svg", :viewbox "0 0 24 24", :fill "currentColor", :class "size-6"} props)
    [:path {:fill-rule "evenodd", :d "M2.25 12c0-5.385 4.365-9.75 9.75-9.75s9.75 4.365 9.75 9.75-4.365 9.75-9.75 9.75S2.25 17.385 2.25 12ZM12 8.25a.75.75 0 0 1 .75.75v3.75a.75.75 0 0 1-1.5 0V9a.75.75 0 0 1 .75-.75Zm0 8.25a.75.75 0 1 0 0-1.5.75.75 0 0 0 0 1.5Z", :clip-rule "evenodd"}]]))

(defn spinner 
  ([] (spinner {}))
  ([props]
   [:svg (merge {:xmlns "http://www.w3.org/2000/svg", :fill "currentColor", :stroke "currentColor", :viewbox "0 0 24 24"} props)
    [:path {:fillrule "evenodd", :d "M12 19a7 7 0 100-14 7 7 0 000 14zm0 3c5.523 0 10-4.477 10-10S17.523 2 12 2 2 6.477 2 12s4.477 10 10 10z", :cliprule "evenodd", :opacity "0.2"}]
    [:path {:d "M2 12C2 6.477 6.477 2 12 2v3a7 7 0 00-7 7H2z"}]]))

(defn eye 
  ([] (eye {}))
  ([props]
   [:svg (merge {:xmlns "http://www.w3.org/2000/svg", :viewbox "0 0 24 24", :fill "currentColor", :class "size-6"} props)
    [:path {:d "M12 15a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z"}]
    [:path {:fill-rule "evenodd", :d "M1.323 11.447C2.811 6.976 7.028 3.75 12.001 3.75c4.97 0 9.185 3.223 10.675 7.69.12.362.12.752 0 1.113-1.487 4.471-5.705 7.697-10.677 7.697-4.97 0-9.186-3.223-10.675-7.69a1.762 1.762 0 0 1 0-1.113ZM17.25 12a5.25 5.25 0 1 1-10.5 0 5.25 5.25 0 0 1 10.5 0Z", :clip-rule "evenodd"}]]))

(defn eye-slash
  ([] (eye-slash {}))
  ([props]
   [:svg (merge {:xmlns "http://www.w3.org/2000/svg", :viewbox "0 0 24 24", :fill "currentColor", :class "size-6"} props)
    [:path {:d "M3.53 2.47a.75.75 0 0 0-1.06 1.06l18 18a.75.75 0 1 0 1.06-1.06l-18-18ZM22.676 12.553a11.249 11.249 0 0 1-2.631 4.31l-3.099-3.099a5.25 5.25 0 0 0-6.71-6.71L7.759 4.577a11.217 11.217 0 0 1 4.242-.827c4.97 0 9.185 3.223 10.675 7.69.12.362.12.752 0 1.113Z"}]
    [:path {:d "M15.75 12c0 .18-.013.357-.037.53l-4.244-4.243A3.75 3.75 0 0 1 15.75 12ZM12.53 15.713l-4.243-4.244a3.75 3.75 0 0 0 4.244 4.243Z"}]
    [:path {:d "M6.75 12c0-.619.107-1.213.304-1.764l-3.1-3.1a11.25 11.25 0 0 0-2.63 4.31c-.12.362-.12.752 0 1.114 1.489 4.467 5.704 7.69 10.675 7.69 1.5 0 2.933-.294 4.242-.827l-2.477-2.477A5.25 5.25 0 0 1 6.75 12Z"}]]))