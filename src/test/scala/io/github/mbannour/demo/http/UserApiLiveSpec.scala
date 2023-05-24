package io.github.mbannour.demo.http

import io.github.mbannour.MongoZioCollection
import io.github.mbannour.demo.model.{Address, Customer, CustomerBody}
import io.github.mbannour.demo.repository.CustomerRepository
import io.github.mbannour.demo.results.MongoFakeResults
import io.github.mbannour.result.{DeleteResult, InsertOneResult, UpdateResult}
import org.bson.types.ObjectId
import zio._
import zio.test._
import zio.test.Assertion._
import zio.http._
import zio.json._

object CustomerApiSpec extends ZIOSpecDefault {
  lazy val mockCustomerRepository = new CustomerRepository {
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

    override def collection: MongoZioCollection[Customer] = MongoZioCollection(null)

    override def insertCustomer(customer: Customer): IO[Throwable, InsertOneResult] =
      ZIO.succeed(MongoFakeResults.acknowledgedInsertResult())

    override def getCustomers(): IO[Throwable, Iterator[Customer]] = ZIO.succeed(Iterator(customer))

    override def getCustomerById(customerId: ObjectId): IO[Throwable, Option[Customer]] =
      ZIO.succeed(Some(customer))

    override def updateCustomer(
        customerId: ObjectId,
        customer: CustomerBody,
      ): IO[Throwable, UpdateResult] =
      ZIO.succeed(MongoFakeResults.acknowledgedUpdateResult())

    override def deleteCustomer(customerId: ObjectId): IO[Throwable, DeleteResult] =
      ZIO.succeed(MongoFakeResults.acknowledgedDeleteOneResult())
  }

  lazy val customerRepositoryLayer = ZLayer.fromZIO(ZIO.attempt(mockCustomerRepository))

  def spec = suite("CustomerApiSpec")(
    test("Should successfully create a customer") {
      val customerBody = CustomerBody(
        "John Doe",
        25,
        "john@example.com",
        "123456789",
        Some("123456789"),
        Address("", "", 2323),
      )
      val customerAPI  = CustomerApiLive(mockCustomerRepository)
      val request = Request.post(Body.fromString(customerBody.toJson), url = URL(!! / "customers"))
      for {
        response <- customerAPI.httpApp.runZIO(request)
      } yield assert(response.status)(equalTo(Status.Created))
    },
    test("Should successfully delete customer") {
      val api     = CustomerApiLive(mockCustomerRepository)
      val request = Request.delete(url =
        URL(!! / "customers" / s"${mockCustomerRepository.customer._id.toHexString}"),
      )
      for {
        response <- api.httpApp.runZIO(request)
      } yield assert(response.status)(equalTo(Status.Ok))
    },
    test("Should successfully updated customer") {
      val api          = CustomerApiLive(mockCustomerRepository)
      val customerBody = CustomerBody(
        "John ",
        30,
        "test@example.com",
        "123456789",
        Some("123456789"),
        Address("street", "34a", 2323),
      )

      val request = Request.put(
        body = Body.fromString(customerBody.toJson),
        url = URL(!! / "customers" / s"${mockCustomerRepository.customer._id.toHexString}"),
      )
      for {
        response <- api.httpApp.runZIO(request)
      } yield assert(response.status)(equalTo(Status.Ok))
    },
    test("Should successfully get customer") {
      val api     = CustomerApiLive(mockCustomerRepository)
      val request = Request.get(url =
        URL(!! / "customers" / s"${mockCustomerRepository.customer._id.toHexString}"),
      )
      for {
        response <- api.httpApp.runZIO(request)
      } yield assert(response.status)(equalTo(Status.Ok))
    },
    test("Should successfully get all customers") {
      val api     = CustomerApiLive(mockCustomerRepository)
      val request = Request.get(url = URL(!! / "customers"))
      for {
        response <- api.httpApp.runZIO(request)
      } yield assert(response.status)(equalTo(Status.Ok))
    },
  ).provideShared(customerRepositoryLayer)
}
