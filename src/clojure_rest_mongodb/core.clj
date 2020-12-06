(ns clojure-rest-mongodb.core
  (:require ;[clojure-rest-mongodb.metadata.model :as reviews]
   [clojure-rest-mongodb.metadata.handler :refer [handle-metadatas
                                                  handle-get-book
                                                  handle-create-book
                                                  handle-delete-book
                                                  handle-related-images
                                                  handle-create-log
                                                  handle-read-logs]])
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
  {:body "
          <h1>Available endpoints (kindle book metadata) - MongoDB</h1>
          <p>
          <b>/api/get_metadatas:</b>
          READ - to get metadata of books (with specified limit)
          </p>

          <p>
          <b>/api/get_metadata:</b>
          READ - to get metadata of a specific book
          </p>

          <p>
          <b>/api/create_book:</b>
          CREATE - add a book (metadata)          
          </p>

          <p>
          <b>/api/delete_book:</b>
          DELETE - remove a book
          </p>

          <p>
          <b>/api/related_images:</b>
          READ - get the image urls of related images
          </p>

          "})

; REST routes
(defroutes routes
  (GET "/" [] root) ; Shows available API endpoints
  (GET "/request" [] handle-dump)

  (POST "/api/get_metadatas" [] handle-metadatas)
  (POST "/api/get_metadata" [] handle-get-book)
  (POST "/api/create_book" [] handle-create-book)
  (POST "/api/delete_book" [] handle-delete-book)
  (POST "/api/related_images" [] handle-related-images)

  (POST "/api/create_log" [] handle-create-log)
  (GET "/api/read_logs" [] handle-read-logs)

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