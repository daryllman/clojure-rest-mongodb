(ns clojure-rest-mongodb.metadata.model
  (:import org.bson.types.ObjectId)
  (:refer-clojure :exclude [sort find])
  (:require [clojure.string :as s])

  (:require [monger.core :as mg]
            [monger.query :refer :all]
            [monger.collection :as mc])
  ;(:import [com.mongodb MongoOptions ServerAddress])
  )
;___________________________________________________________________________________
; Read metadatas - To get metadata of books (with specified limit) db.metadata.find().skip(5).limit(5)
; e.g. db.metadata.find ().skip (5).limit (5)
(defn read-metadatas [db skipNum limitNum category title author]
  (if (some? category)
    (mc/aggregate db "metadata" [{"$project" {:_id 0}} ; with category
                                 ;{"$unwind" {:path "$categories"}}
                                 {"$addFields" {:categories {"$reduce" {:input "$categories" :initialValue (list) :in {"$concatArrays" (list "$$value" "$$this")}}}}}
                                 {"$match" {:categories {"$in" (list category)}}}
                                 {"$match" {:title {"$not" {"$eq" Double/NaN}}}}
                                 {"$skip" skipNum}
                                 {"$limit" limitNum}]
                  :cursor {})
    (mc/aggregate db "metadata" [{"$project" {:_id 0}} ; default - max
                                 {"$match" {:title {"$not" {"$eq" Double/NaN}}}}
                                 {"$skip" skipNum}
                                 {"$limit" limitNum}
                                 ;{"$unwind" {:path "$categories"}}
                                 {"$addFields" {:categories {"$reduce" {:input "$categories" :initialValue (list) :in {"$concatArrays" (list "$$value" "$$this")}}}}}]
                  :cursor {}))
  (cond
    (and (s/blank? category) (s/blank? title) (s/blank? author)) (mc/aggregate db "metadata" [{"$project" {:_id 0}} ; default - max
                                                                                              {"$match" {:title {"$not" {"$eq" Double/NaN}}}}
                                                                                              {"$skip" skipNum}
                                                                                              {"$limit" limitNum}
                                 ;{"$unwind" {:path "$categories"}}
                                                                                              {"$addFields" {:categories {"$reduce" {:input "$categories" :initialValue (list) :in {"$concatArrays" (list "$$value" "$$this")}}}}}]
                                                                               :cursor {})
    (and (not (s/blank? category)) (s/blank? title) (s/blank? author)) (mc/aggregate db "metadata" [{"$project" {:_id 0}} ; with category
                                 ;{"$unwind" {:path "$categories"}}
                                                                                                    {"$addFields" {:categories {"$reduce" {:input "$categories" :initialValue (list) :in {"$concatArrays" (list "$$value" "$$this")}}}}}
                                                                                                    {"$match" {:categories {"$in" (list category)}}}
                                                                                                    {"$match" {:title {"$not" {"$eq" Double/NaN}}}}
                                                                                                    {"$skip" skipNum}
                                                                                                    {"$limit" limitNum}]
                                                                                     :cursor {})
    (and (s/blank? category) (not (s/blank? title)) (s/blank? author)) (mc/aggregate db "metadata" [{"$project" {:_id 0}} ; with category
                                 ;{"$unwind" {:path "$categories"}}
                                                                                                    {"$addFields" {:categories {"$reduce" {:input "$categories" :initialValue (list) :in {"$concatArrays" (list "$$value" "$$this")}}}}}

                                                                                                    {"$match" {:title {"$not" {"$eq" Double/NaN}}}}
                                                                                                    {"$match" {:title title}}
                                                                                                    ;{"$skip" skipNum}
                                                                                                    ;{"$limit" limitNum}
                                                                                                    ]
                                                                                     :cursor {})
    (and (s/blank? category) (s/blank? title) (not (s/blank? author))) (mc/aggregate db "metadata" [{"$project" {:_id 0}} ; with category
                                 ;{"$unwind" {:path "$categories"}}
                                                                                                    {"$addFields" {:categories {"$reduce" {:input "$categories" :initialValue (list) :in {"$concatArrays" (list "$$value" "$$this")}}}}}

                                                                                                    {"$match" {:title {"$not" {"$eq" Double/NaN}}}}
                                                                                                    {"$match" {:author author}}
                                                                                                    ;{"$skip" skipNum}
                                                                                                    ;{"$limit" limitNum}
                                                                                                    ]
                                                                                     :cursor {})
    (and (not (s/blank? category)) (not (s/blank? title)) (s/blank? author)) (mc/aggregate db "metadata" [{"$project" {:_id 0}} ; with category
                                 ;{"$unwind" {:path "$categories"}}
                                                                                                          {"$addFields" {:categories {"$reduce" {:input "$categories" :initialValue (list) :in {"$concatArrays" (list "$$value" "$$this")}}}}}
                                                                                                          {"$match" {:categories {"$in" (list category)}}}
                                                                                                          {"$match" {:title title}}
                                                                                                          {"$match" {:title {"$not" {"$eq" Double/NaN}}}}
                                                                                                          ;{"$skip" skipNum}
                                                                                                          ;{"$limit" limitNum}
                                                                                                          ]
                                                                                           :cursor {})
    (and (not (s/blank? category)) (s/blank? title) (not (s/blank? author))) (mc/aggregate db "metadata" [{"$project" {:_id 0}} ; with category
                                 ;{"$unwind" {:path "$categories"}}
                                                                                                          {"$addFields" {:categories {"$reduce" {:input "$categories" :initialValue (list) :in {"$concatArrays" (list "$$value" "$$this")}}}}}
                                                                                                          {"$match" {:categories {"$in" (list category)}}}
                                                                                                          {"$match" {:author author}}
                                                                                                          {"$match" {:title {"$not" {"$eq" Double/NaN}}}}
                                                                                                          ;{"$skip" skipNum}
                                                                                                          ;{"$limit" limitNum}
                                                                                                          ]
                                                                                           :cursor {})
    (and (not (s/blank? category)) (not (s/blank? title)) (not (s/blank? author))) (mc/aggregate db "metadata" [{"$project" {:_id 0}} ; with category
                                 ;{"$unwind" {:path "$categories"}}
                                                                                                                {"$addFields" {:categories {"$reduce" {:input "$categories" :initialValue (list) :in {"$concatArrays" (list "$$value" "$$this")}}}}}
                                                                                                                {"$match" {:categories {"$in" (list category)}}}
                                                                                                                {"$match" {:title title}}
                                                                                                                {"$match" {:author author}}
                                                                                                                {"$match" {:title {"$not" {"$eq" Double/NaN}}}}
                                                                                                                ;{"$skip" skipNum}
                                                                                                                ;{"$limit" limitNum}
                                                                                                                ]
                                                                                                 :cursor {})))
;___________________________________________________________________________________
; Read metadata of a book
(defn read-book [db asin]
  (nth (mc/aggregate db "metadata" [{"$match" {:title {"$not" {"$eq" Double/NaN}}}}
                                    {"$match" {:asin asin}}
                                    {"$project" {:_id 0}}
                                    {"$unwind" {:path "$categories"}}]
                     :cursor {}) 0))
;___________________________________________________________________________________
; Create a book
(defn create-book [db title author description price imUrl categories]

  (let [oid (ObjectId.)]

    (mc/insert db "metadata" {:_id oid :title title :author author :description description :price price :imUrl imUrl :categories (list categories)})  ; categories is a 2D array

    (let [id (:id (nth (mc/aggregate db "metadata" [{"$sort" {:_id -1}}
                                                    {"$limit" 1}
                                                    {"$project" {:id {"$toString" "$_id"} :_id 0}}]
                                     :cursor {}) 0))]
      (mc/update-by-id db "metadata" oid {"$set" {:asin id}})
      (println "Created book with asin: " id)) ;{"$set" {:asin id}}
    :cursor {})
  (nth (mc/aggregate db "metadata" [{"$sort" {:_id -1}}
                                    {"$limit" 1}
                                    {"$unwind" {:path "$categories"}}
                                    {"$project" {:_id 0}}]
                     :cursor {}) 0))
;___________________________________________________________________________________
; Delete a specified book
(defn delete-book [db asin]
  (mc/remove db "metadata" {:asin asin})
  (println "Removed booked with asin: " asin))
;___________________________________________________________________________________
; Get Related Images of a specified book
(defn related-images [db asin max]
  (println "Returning related images of asin: " asin)
  (if (some? max)
    (nth (mc/aggregate db "metadata" [{"$match" {:asin asin}}
                                      ;{"$project" {:related {"$concatArrays" (list "$related.also_viewed" "$related.buy_after_viewing")} :_id 0}}
                                      {"$project" {:related {"$concatArrays" (list {"$ifNull" (list "$related.also_viewed" [])} {"$ifNull" (list "$related.buy_after_viewing" [])} {"$ifNull" (list "$related.also_bought" [])})} :_id 0}}
                                      {"$project" {:related {"$slice" (list "$related" 0 (+ max 1))}}}
                                      {"$lookup" {:from "metadata" :localField "related" :foreignField "asin" :as "images"}}
                                               ;{"$project" {:images "$images.imUrl"}}
                                      {"$project" {:imUrlList "$images.imUrl" :asinList "$images.asin"}}]
                       :cursor {}) 0)
    (nth (mc/aggregate db "metadata" [{"$match" {:asin asin}}
                                      {"$project" {:related {"$concatArrays" (list {"$ifNull" (list "$related.also_viewed" [])} {"$ifNull" (list "$related.buy_after_viewing" [])} {"$ifNull" (list "$related.also_bought" [])})} :_id 0}}
                                               ;{"$project" {:related {"$slice" (list "$related" 0 (+ max 1))}}}
                                      {"$lookup" {:from "metadata" :localField "related" :foreignField "asin" :as "images"}}
                                               ;{"$project" {:images "$images.imUrl"}}
                                      {"$project" {:imUrlList "$images.imUrl" :asinList "$images.asin"}}]
                       :cursor {}) 0)))





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


