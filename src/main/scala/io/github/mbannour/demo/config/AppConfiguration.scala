package io.github.mbannour.demo.config

import pureconfig._
import pureconfig.generic.auto._

final case class MongoDatabaseConfiguration(url: String)

object AppConfiguration {

  def loadMongoConfig(cs: ConfigSource = ConfigSource.default): MongoDatabaseConfiguration = {
      cs.at("mongo").loadOrThrow[MongoDatabaseConfiguration]
  }

}
