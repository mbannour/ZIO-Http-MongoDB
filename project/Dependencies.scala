import sbt._

object Dependencies {
  val ZioVersion   = "2.0.13"
  val ZHTTPVersion = "3.0.0-RC1"
  val LogbackVersion        = "1.2.11"

  val `zio-http`      = "dev.zio" %% "zio-http" % ZHTTPVersion
  val mongoZio =     "io.github.mbannour" %% "ziomongo" % "0.0.6"
  val `zio-http-test` =  "dev.zio" %% "zio-http-testkit" %  ZHTTPVersion % Test
  val pureConfig = "com.github.pureconfig" %% "pureconfig" % "0.17.4"

  val `zio-test`     = "dev.zio" %% "zio-test"     % ZioVersion % Test
  val `zio-test-sbt` = "dev.zio" %% "zio-test-sbt" % ZioVersion % Test
  val zioJson = "dev.zio"       %% "zio-json"            % "0.5.0"
  val zioMock =  "dev.zio" %% "zio-mock" % "1.0.0-RC10" % Test

  lazy val logging = Seq(
    "ch.qos.logback" % "logback-core" % LogbackVersion,
    "ch.qos.logback" % "logback-classic" % LogbackVersion,
    "dev.zio" %% "zio-logging" % "2.1.13",
    "org.slf4j" % "slf4j-api" % "1.7.32"
  )
}
