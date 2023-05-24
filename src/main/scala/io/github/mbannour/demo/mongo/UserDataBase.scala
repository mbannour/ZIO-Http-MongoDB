package io.github.mbannour.demo.mongo

import io.github.mbannour.demo.model.{Address, Customer}
import io.github.mbannour.{MongoZioClient, MongoZioDatabase}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import zio.{ZIO, ZLayer}

case class ApplicationDatabaseLive(client: MongoZioClient) extends ApplicationDatabase {
  def getDatabase: MongoZioDatabase = client
    .getDatabase("application")
    .withCodecRegistry(fromRegistries(fromProviders(classOf[Customer], classOf[Address]), DEFAULT_CODEC_REGISTRY))
}

trait ApplicationDatabase {
  def getDatabase: MongoZioDatabase
}

object ApplicationDatabase {
  lazy val live = ZLayer {
    for {
      client <- ZIO.service[MongoZioClient]
    } yield (ApplicationDatabaseLive(client))
  }
}
