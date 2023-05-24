package io.github.mbannour.demo

import io.github.mbannour.MongoZioClient
import io.github.mbannour.demo.config.AppConfiguration
import io.github.mbannour.demo.http.CustomerApi
import io.github.mbannour.demo.mongo.ApplicationDatabase
import io.github.mbannour.demo.repository.CustomerRepository
import zio._
import zio.http._

object Main extends ZIOAppDefault {

  val liveClient : ZLayer[Any, Throwable, MongoZioClient] = ZLayer.scoped {
    for {
      config <- ZIO.attempt(AppConfiguration.loadMongoConfig()).orDie
      client <- ZIO.logInfo("start MongoDB connection") *>
        MongoZioClient(config.url).retry(Schedule.recurs(3) && Schedule.spaced(30.seconds))
    } yield client
  }

  val app = ZIO
    .serviceWithZIO[CustomerApi](customerApi => Server.serve(customerApi.httpApp.withDefaultErrorResponse))
    .provide(
      Server.defaultWithPort(8080),
      liveClient,
      ApplicationDatabase.live,
      CustomerRepository.live,
      CustomerApi.live,
      //      ZLayer.Debug.tree,
    )

  override def run = app.exitCode
}

