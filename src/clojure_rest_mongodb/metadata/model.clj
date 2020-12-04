(ns clojure-rest-mongodb.metadata.model
  (:import org.bson.types.ObjectId)
  (:refer-clojure :exclude [sort find])

  (:require [monger.core :as mg]
            [monger.query :refer :all]
            [monger.collection :as mc])
  ;(:import [com.mongodb MongoOptions ServerAddress])
  )
;___________________________________________________________________________________
; Read metadatas - To get metadata of books (with specified limit) db.metadata.find().skip(5).limit(5)
; e.g. db.metadata.find ().skip (5).limit (5)
(defn read-metadatas [db skipNum limitNum category]
  (if (some? category)
    (mc/aggregate db "metadata" [{"$project" {:_id 0}}
                                 {"$unwind" {:path "$categories"}}
                                 {"$match" {:categories {"$in" (list category)}}}
                                 {"$skip" skipNum}
                                 {"$limit" limitNum}]
                  :cursor {})
    (mc/aggregate db "metadata" [{"$project" {:_id 0}}
                                 {"$unwind" {:path "$categories"}}
                                 {"$skip" skipNum}
                                 {"$limit" limitNum}]
                  :cursor {})))
;___________________________________________________________________________________
; Read metadata of a book
(defn read-book [db asin]
  (mc/aggregate db "metadata" [{"$match" {:asin asin}}
                               {"$project" {:_id 0}}
                               {"$unwind" {:path "$categories"}}]
                :cursor {}))
;___________________________________________________________________________________
; Create a book
(defn create-book [db title description price imUrl categories]

  (let [oid (ObjectId.)]

    (mc/insert db "metadata" {:_id oid :title title  :description description :price price :imUrl imUrl :categories (list categories)})  ; categories is a 2D array

    (let [id (:id (nth (mc/aggregate db "metadata" [{"$sort" {:_id -1}}
                                                    {"$limit" 1}
                                                    {"$project" {:id {"$toString" "$_id"} :_id 0}}]
                                     :cursor {}) 0))]
      (mc/update-by-id db "metadata" oid {"$set" {:asin id}})
      (println "++++++++++++++++++++ Created book with asin: " id)) ;{"$set" {:asin id}}
    :cursor {})
  (mc/aggregate db "metadata" [{"$sort" {:_id -1}}
                               {"$limit" 1}
                               {"$unwind" {:path "$categories"}}
                               {"$project" {:_id 0}}]
                :cursor {}))

; Delete a specified book
;; (defn delete-book [db asin]
;;   (#(dissoc % :_id)  (nth (with-collection db "metadata"
;;                             (find {:asin asin})) 0)))
;___________________________________________________________________________________
(defn delete-book [db categories]
  (mc/aggregate db "metadata" [{"$project" {:_id 0}}
                               {"$unwind" {:path "$categories"}}
                                    ;{"$match" {"$in" (list "Literature & Fiction")}}
                               ;{"$match" {:categories (hash-map "$in" (list "Literature & Fiction"))}}
                               {"$match" {:categories {"$in" (list categories)}}}
                               {"$skip" 5}
                               {"$limit" 5}]
                :cursor {}))
;___________________________________________________________________________________


;;;; Archive of past work

;; (defn delete-book [db categories]
;;   (if (some? categories)
;;     (mc/aggregate db "metadata" [{"$project" {:_id 0}}
;;                                  {"$unwind" {:path "$categories"}}
;;                                     ;{"$match" {"$in" (list "Literature & Fiction")}}
;;                                ;{"$match" {:categories (hash-map "$in" (list "Literature & Fiction"))}}
;;                                  {"$match" {:categories {"$in" (list categories)}}}
;;                                  {"$skip" 5}
;;                                  {"$limit" 5}]
;;                   :cursor {})
;;     (mc/aggregate db "metadata" [{"$project" {:_id 0}}
;;                                  {"$unwind" {:path "$categories"}}
;;                                     ;{"$match" {"$in" (list "Literature & Fiction")}}
;;                                ;{"$match" {:categories (hash-map "$in" (list "Literature & Fiction"))}}
;;                                  {"$match" {:categories {"$in" (list "Books")}}}
;;                                  {"$skip" 5}
;;                                  {"$limit" 5}]
;;                   :cursor {})))
;;                   

;; (defn read-metadatas [db skipNum limitNum]
;;   ;(dissoc (nth (mc/find-maps db "metadata" {:asin "B000FA5ZEG"}) 0) :_id)
;;   ;(dissoc (nth (mc/find-maps db "metadata" {:asin "B000FA5ZEG"}) 0) :_id)
;; ;;   (dissoc (nth (with-collection db "metadata"
;; ;;                  (find {})
;; ;;                  (skip 5)
;; ;;                  (limit 5)) 0) :_id)

;;   (map #(dissoc % :_id)  (with-collection db "metadata"
;;                            (find {})
;;                            (skip skipNum)
;;                            (limit limitNum))))


