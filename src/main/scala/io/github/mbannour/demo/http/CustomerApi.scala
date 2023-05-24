package io.github.mbannour.demo.http

import io.github.mbannour.demo.model.{Customer, CustomerBody}
import io.github.mbannour.demo.repository.CustomerRepository
import org.bson.types.ObjectId
import zio._
import zio.http.Status.{BadRequest, Created}
import zio.http._
import zio.json._

trait CustomerApi {
  def httpApp: Http[CustomerRepository, Throwable, Request, Response]
}

final case class CustomerApiLive(customerRepo: CustomerRepository) extends CustomerApi {

  override def httpApp: Http[CustomerRepository, Throwable, Request, Response] =
    Http.collectZIO[Request] {

      case req @ Method.POST -> !! / "customers" =>
        req.body.asString.map(_.fromJson[CustomerBody]).flatMap {
          case Left(e)     =>
            ZIO
              .logErrorCause(s"Failed to parse the input: $e", Cause.fail(e))
              .as(
                Response.status(BadRequest),
              )
          case Right(customer) =>
            customerRepo
              .insertCustomer(
                Customer(
                  customer.name,
                  customer.age,
                  customer.email,
                  customer.phoneNumber,
                  customer.phoneWork,
                  customer.address,
                ),
              ).foldCauseZIO(
                failure =>
                  ZIO
                    .logErrorCause(s"Failed insert customer", Cause.fail(failure))
                    .as(Response.status(Status.InternalServerError)),
                _ =>
                  ZIO
                    .logInfo(s"all customers retrieved successfully")
                    .as(Response.status(Status.Created)),
              )
        }

      case Method.GET -> !! / "customers" =>
        customerRepo
          .getCustomers()
          .foldCauseZIO(
            failure =>
              ZIO
                .logErrorCause(s"Failed to get all customers", Cause.fail(failure))
                .as(Response.status(Status.InternalServerError)),
            customers =>
              ZIO
                .logInfo(s"all customers retrieved successfully")
                .as(Response.json(customers.toList.toJson)),
          )

      case Method.GET -> !! / "customers" / id =>
        ZIO
          .attempt(new ObjectId(id))
          .flatMap(id => customerRepo.getCustomerById(id))
          .foldCauseZIO(
            failure =>
              ZIO
                .logErrorCause(s"Failed to read customer", Cause.fail(failure))
                .as(Response.status(Status.NotFound)),
            customer => ZIO.logInfo(s"customer read: $id").as(Response.json(customer.toJson)),
          )

      case Method.DELETE -> !! / "customers" / id =>
        ZIO
          .attempt(new ObjectId(id))
          .flatMap(id => customerRepo.deleteCustomer(id))
          .foldCauseZIO(
            failure =>
              ZIO
                .logErrorCause(s"Failed to delete customer", Cause.fail(failure))
                .as(Response.status(Status.InternalServerError)),
            _ => ZIO.logInfo(s"customer deleted : $id").as(Response.status(Status.Ok)),
          )

      case req @ Method.PUT -> !! / "customers" / id =>
        req.body.asString.map(_.fromJson[CustomerBody]).flatMap {
          case Left(e)  =>
            ZIO
              .logErrorCause("Failed to delete customer", Cause.fail(e))
              .as(Response.status(Status.InternalServerError))
          case Right(customer) =>
            ZIO
              .attempt(new ObjectId(id))
              .flatMap(id => customerRepo.updateCustomer(id, customer))
              .foldCauseZIO(
                failure =>
                  ZIO
                    .logErrorCause(s"Failed to update customer", Cause.fail(failure))
                    .as(Response.status(Status.InternalServerError)),
                _ => ZIO.logInfo(s"customer updated : $id").as(Response.status(Status.Ok)),
              )
        }
    }
}

object CustomerApi {
  lazy val live: ZLayer[CustomerRepository , Nothing, CustomerApi] = ZLayer {
    for {
      customerRepo <- ZIO.service[CustomerRepository]
    } yield CustomerApiLive(customerRepo)
  }
}
