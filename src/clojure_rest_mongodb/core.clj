(ns clojure-rest-mongodb.core
  (:require ;[clojure-rest-mongodb.metadata.model :as reviews]
   [clojure-rest-mongodb.metadata.handler :refer [handle-metadatas
                                                  handle-get-book
                                                          ;;  handle-create-review
                                                          ;;  handle-reviews
                                                          ;;  handle-delete-review
                                                  ]])
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [compojure.core :refer [defroutes ANY GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]])
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [clojure.data.json :as json]
            [ring.util.response :refer [response]])
  (:import [com.mongodb MongoOptions ServerAddress]))

; Initialise database -> datasource
; (def db {:dbtype "mysql" :dbname "kindle-reviews" :user "root" :password "1234" :serverTimezone "UTC"})
; (def ds (jdbc/get-datasource db))

; Initialise MongoDB database
(def conn (mg/connect))
(def db   (mg/get-db conn "kindle"))



(defn root [req]
  ;; {:body "
  ;;         <h1>Available endpoints (kindle book metadata) - MongoDB</h1>
  ;;         <p>
  ;;         <b>/api/all_reviews:</b>
  ;;         (this api will not be used) To retrieve all the available reviews from database
  ;;         </p>

  ;;         <p>
  ;;         <b>/api/get_reviews:</b>
  ;;         Get all reviews of a particular book
  ;;         </p>

  ;;         <p>
  ;;         <b>/api/create_review:</b>
  ;;         Add a review to a particular book - requires asin, helpful overall, reviewText, reviewTime, reviewerID, reviewerName, summary, unixReviewTime
  ;;         </p>

  ;;         <p>
  ;;         <b>/api/delete_review:</b>
  ;;         Remove a review to a particular book - requires asin, reviewerID, unixReviewTime
  ;;         </p>
  ;;         <hr/>
  ;;         <h2>Table Model:</h2>
  ;;         <p>asin char(10)</p>
  ;;         <p>helpful varchar(10) DEFAULT NULL</p>
  ;;         <p>overall integer(1) DEFAULT NULL</p>
  ;;         <p>reviewText text(1000)</p>
  ;;         <p>reviewTime varchar(11)</p>
  ;;         <p>reviewerID varchar(14)</p>
  ;;         <p>reviewerName varchar(64)</p>
  ;;         <p>summary varchar(255) DEFAULT NULL</p>
  ;;         <p>unixReviewTime integer(10)</p>
  ;;         <p>PRIMARY KEY(asin, reviewerID, unixReviewTime)</p>
  ;;         "}
  {:body (list (json/write-str (response (dissoc (nth (mc/find-maps db "metadata" {:asin "B000FA5ZEG"}) 0) :_id))))})

; REST routes
(defroutes routes
  (GET "/" [] root) ; Shows available API endpoints
  (GET "/request" [] handle-dump)

  (POST "/api/get_metadatas" [] handle-metadatas)
  (POST "/api/get_metadata" [] handle-get-book)
  ; (GET "/api/all_reviews" [] handle-all-reviews) ; this api will not be used - just to quickly display all reviews (if db is set up correctly)
  ; (POST "/api/get_reviews" [] handle-reviews) ; Get all review of a particular book
  ; (POST "/api/create_review" [] handle-create-review) ; Add a review to a particular book
  ; (POST "/api/delete_review" [] handle-delete-review) ; Remove a review to a particular book - requires asin, reviewerID, unixReviewTime

  (not-found "Page not found"))

(defn wrap-db [hdlr]
  (fn [req]
    (hdlr (assoc req :clojure-rest-mongodb/db db))))

(defn wrap-server [hdlr]
  (fn [req]
    (assoc-in (hdlr req) [:headers "Server"] "Kindle Books Metadata MongoDB Server")))

(def app
  (wrap-server (wrap-db (wrap-json-params routes))))
;; (def app
;;   (wrap-server  (wrap-json-params routes)))

(defn -main [port]
  ; (reviews/create-table ds)
  (jetty/run-jetty app {:port (Integer. port)}))

(defn -dev-main [port]
  ; (reviews/create-table ds)
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))