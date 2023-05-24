package io.github.mbannour.demo.config

import pureconfig.ConfigSource
import zio.test._
import zio.test.Assertion._

class AppConfigurationSpec extends ZIOSpecDefault {
  def spec = suite("AppConfigurationSpec")(
    test("Should return configured url") {

      val urlConfig = """mongo {
                         | url = "mongodb://localhost:27017"
                         |}""".stripMargin

      val urlConfigSource = ConfigSource.string(urlConfig)

      assert(AppConfiguration.loadMongoConfig(urlConfigSource).url)(
        equalTo("mongodb://localhost:27017"),
      )
    },
  )
}
