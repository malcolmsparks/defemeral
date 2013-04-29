;; Copyright © 2013, Malcolm Sparks. All Rights Reserved.

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be found
;; in the file epl-v10.html at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the
;; terms of this license.  You must not remove this notice, or any
;; other, from this software.

(ns ^{:doc "Global ephemeral states with definite lifespans"
      :author "Malcolm Sparks"}
  defemeral.defemeral)

(defprotocol Ephemeral
  "Anything that has a beginning and an end."
  (begin [_])
  (end [_ payload]))

(def ephemerals (atom {}))

(defn shutdown []
  (doseq [[k [v pl]] @ephemerals]
    (swap! ephemerals dissoc k)
    (end v pl)))

(defn add-ephemeral
  "If the var is already known to reference an existing ephemeral, end
  this first prior to registering a new ephemeral against the var and
  beginning it."
  [^clojure.lang.Var eph]
  {:pre [(map? @ephemerals) (var? eph)]
   :post [(contains? @ephemerals eph)]}
  
  (when-let [[_ [old payload]] (find @ephemerals eph)]
    (end old payload)
    (swap! ephemerals dissoc eph))
  
  (swap! ephemerals assoc eph [@eph (begin @eph)]))


(defmacro defemeral
  "A macro to create a global ephemeral against a var, and register in the
  ephemerals map."
  [name & body]
  `(let []
     (def ~name (reify Ephemeral ~@body))
     (add-ephemeral (var ~name))))
