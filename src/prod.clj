(ns prod
  (:gen-class)
  (:require app.todo-list ; prod only (app is not loaded by shadow in prod)
            [datahike.api :as d]
            clojure.string
            app.electric-server-java8-jetty9))

(def electric-server-config
  {:host "0.0.0.0", :port 8080, :resources-path "public"})

(defn start-datahike! []
  (let [cfg {:store {:backend :mem :id "schemaless"}
             :schema-flexibility :read}
        _ (d/create-database cfg)
        conn (d/connect cfg)]
    conn))

(def !datahike)


(defn -main [& args] ; run with `clj -M -m prod`
  (when (clojure.string/blank? (System/getProperty "HYPERFIDDLE_ELECTRIC_SERVER_VERSION"))
    (throw (ex-info "HYPERFIDDLE_ELECTRIC_SERVER_VERSION jvm property must be set in prod" {})))
  (alter-var-root #'!datahike (constantly (start-datahike!)))
  (app.electric-server-java8-jetty9/start-server! electric-server-config))

; On CLJS side we reuse src/user.cljs for prod entrypoint