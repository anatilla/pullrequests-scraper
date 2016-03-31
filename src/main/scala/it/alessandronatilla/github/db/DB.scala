package it.alessandronatilla.github.db

import com.mongodb.casbah.MongoClient
import com.typesafe.config.ConfigFactory
import com.mongodb.casbah.Imports._


/**
  * Created by alexander on 09/03/16.
  */
object DB {

  private var collection: MongoCollection = null
  private var connection: MongoClient = null
  private var alreadyInitialized = false

  def persist(obj: MongoDBObject): Unit = {
    if (!alreadyInitialized) throw new RuntimeException("launch .init() before using this!")
    this.collection.insert(obj)
  }

  def close(): Unit = {
    if (this.connection != null) this.connection.close()
  }

  def init(collection: String): Unit = {
    val conf = ConfigFactory.load()
    this.connection = MongoClient(conf.getString("github-pr-robber.db.host"), conf.getInt("github-pr-robber.db.port"))
    val db = this.connection(conf.getString("github-pr-robber.db.database"))
    val coll = db(collection)
    this.collection = coll
    this.alreadyInitialized = true


  }
}
