(ns clojure-rest-mongodb.metadata.handler
  (:require
   [clojure-rest-mongodb.metadata.model :refer [read-metadatas
                                                read-book
                                                create-book
                                                delete-book]]
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
        metadatas (read-metadatas db skipNum limitNum category)]
    {:body  (list (json/write-str (response metadatas)))}))


; handle-get-book <- read-book
(defn handle-get-book [req]
  (let [db (:clojure-rest-mongodb/db req)
        asin (get-in req [:params "asin"])
        metadata (read-book db asin)]
    {:body  (list (json/write-str (response metadata)))}))

; handle-create-book <- create-book
(defn handle-create-book [req]
  (let [db (:clojure-rest-mongodb/db req)
        asin (get-in req [:params "asin"])
        metadata (create-book db asin)]
    {:body  (list (json/write-str (response metadata)))}))


; handle-delete-book <- delete-book
(defn handle-delete-book [req]
  (let [db (:clojure-rest-mongodb/db req)
        categories (get-in req [:params "categories"])
        metadata (delete-book db categories)]
    {:body  (list (json/write-str (response metadata)))}))



