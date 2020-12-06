(ns clojure-rest-mongodb.metadata.handler
  (:require
   [clojure-rest-mongodb.metadata.model :refer [read-metadatas
                                                read-book
                                                create-book
                                                delete-book
                                                related-images]]
   [ring.util.response :refer [response]]
   [clojure.data.json :as json])
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [clojure.data.json :as json]
            [ring.util.response :refer [response]])
  (:import [com.mongodb MongoOptions ServerAddress]))

;; (defn handle-metadatas [req]
;;   (let [db (:clojure-rest-mongodb/db req)
;;         ;skip (get-in req [:params "skip"])
;;         ;limit (get-in req [:params "limit"])
;;         metadatas (read-metadatas db)]
;;     {:body (list (json/write-str (response metadatas)))}))

; handle-metadatas <- read-metadatas
(defn handle-metadatas [req]
  (let [db (:clojure-rest-mongodb/db req)
        skipNum (get-in req [:params "skip"])
        limitNum (get-in req [:params "limit"])
        category (get-in req [:params "category"])
        title (get-in req [:params "title"])
        author (get-in req [:params "author"])
        metadatas (read-metadatas db skipNum limitNum category title author)]
    {:body   (json/write-str (response metadatas))}))


; handle-get-book <- read-book
(defn handle-get-book [req]
  (let [db (:clojure-rest-mongodb/db req)
        asin (get-in req [:params "asin"])
        metadata (read-book db asin)]
    {:body  (list (json/write-str (response metadata)))}))

; handle-create-book <- create-book
(defn handle-create-book [req]
  (let [db (:clojure-rest-mongodb/db req)
        ;asin (get-in req [:params "asin"]) - asin is generated by server side
        title (get-in req [:params "title"])
        description (get-in req [:params "description"])
        price (get-in req [:params "price"])
        imUrl (get-in req [:params "imUrl"])
        categories (get-in req [:params "categories"])
        metadata (create-book db title description price imUrl categories)]
    {:body  (json/write-str (response metadata))}))


; handle-delete-book <- delete-book
(defn handle-delete-book [req]
  (let [db (:clojure-rest-mongodb/db req)
        asin (get-in req [:params "asin"])
        metadata (delete-book db asin)]
    {:body   (json/write-str (response metadata))}))

; handle-related-images <- related-images
(defn handle-related-images [req]
  (let [db (:clojure-rest-mongodb/db req)
        asin (get-in req [:params "asin"])
        max (get-in req [:params "max"])
        imagesList (related-images db asin max)]
    {:body   (list (json/write-str (response imagesList)))}))



