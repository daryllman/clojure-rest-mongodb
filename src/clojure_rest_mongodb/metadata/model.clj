(ns clojure-rest-mongodb.metadata.model
  (:refer-clojure :exclude [sort find])

  (:require [monger.core :as mg]
            [monger.query :refer :all]
            ;[monger.collection :as mc]
            )
  ;(:import [com.mongodb MongoOptions ServerAddress])
  )

; Read metadatas - To get metadata of books (with specified limit) db.metadata.find().skip(5).limit(5)
; e.g. db.metadata.find ().skip (5).limit (5)
(defn read-metadatas [db skipNum limitNum]
  ;(dissoc (nth (mc/find-maps db "metadata" {:asin "B000FA5ZEG"}) 0) :_id)
  ;(dissoc (nth (mc/find-maps db "metadata" {:asin "B000FA5ZEG"}) 0) :_id)
;;   (dissoc (nth (with-collection db "metadata"
;;                  (find {})
;;                  (skip 5)
;;                  (limit 5)) 0) :_id)

  (map #(dissoc % :_id)  (with-collection db "metadata"
                           (find {})
                           (skip skipNum)
                           (limit limitNum))))

(defn read-book [db asin]
  (#(dissoc % :_id)  (nth (with-collection db "metadata"
                            (find {:asin "B000FA5ZEG"})) 0)))