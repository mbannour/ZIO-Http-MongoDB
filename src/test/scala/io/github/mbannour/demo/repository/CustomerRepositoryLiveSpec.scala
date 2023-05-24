package io.github.mbannour.demo.repository

import com.mongodb.{ConnectionString, MongoClientSettings}
import io.github.mbannour.demo.model.{Address, Customer}
import io.github.mbannour.demo.mongo.ApplicationDatabase
import io.github.mbannour.{MongoZioClient, MongoZioDatabase}
import org.bson.codecs.configuration.CodecRegistries._
import org.bson.types.ObjectId
import zio._
import zio.test.ZIOSpecDefault
import zio.test._
import zio.test.Assertion._
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

class CustomerRepositoryLiveSpec extends ZIOSpecDefault {
  lazy val urlConfig: MongoClientSettings =
    MongoClientSettings
      .builder()
      .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
      .build()

  def mongoTestClient(): Task[MongoZioClient] = MongoZioClient(urlConfig)

  val codecRegistry =
    fromRegistries(fromProviders(classOf[Customer], classOf[Address]), DEFAULT_CODEC_REGISTRY)

  val mongoClient = mongoTestClient()

  val database = mongoClient.map { mongoClient =>
    mongoClient.getDatabase("mydb").withCodecRegistry(codecRegistry)
  }

  override def aspects =
    Chunk(
      TestAspect.executionStrategy(ExecutionStrategy.Sequential),
      TestAspect.timeout(Duration.Infinity),
    )

  def spec = suite("CustomerRepositoryLiveSpec")(
    countSavedCustomer()
  )

  private def countSavedCustomer(): Spec[Any, Throwable] = {
    val address = Address(street = "Baker Street", houseNumber = "221B", code = 12345)

    val customer = Customer(
      _id = new ObjectId(),
      name = "John Doe",
      age = 30,
      email = "john.doe@example.com",
      phoneNumber = "1234567890",
      phoneWork = Some("0987654321"),
      address = address,
    )

    val customerRepository = for {
      db <- database
    } yield CustomerRepositoryLive(new ApplicationDatabase {
      override def getDatabase: MongoZioDatabase = db
    })

    val action =
      customerRepository.flatMap(_.insertCustomer(customer)) *> customerRepository.flatMap(
        _.getCustomers(),
      )
    test("Count saved customers") {
      assertZIO(action.map(_.length))(equalTo(1))
    }
  }


}
