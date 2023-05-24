package io.github.mbannour.demo.repository

import io.github.mbannour.MongoZioCollection
import io.github.mbannour.demo.model.{Customer, CustomerBody}
import io.github.mbannour.demo.mongo.ApplicationDatabase
import io.github.mbannour.result.{DeleteResult, InsertOneResult, UpdateResult}
import org.bson.types.ObjectId
import zio.{IO, ZIO, ZLayer}

trait CustomerRepository {

  def collection: MongoZioCollection[Customer]

  def insertCustomer(customer: Customer): IO[Throwable, InsertOneResult]

  def getCustomers(): IO[Throwable, Iterator[Customer]]

  def getCustomerById(customerId: ObjectId): IO[Throwable, Option[Customer]]

  def updateCustomer(customerId: ObjectId, customer: CustomerBody): IO[Throwable, UpdateResult]

  def deleteCustomer(customerId: ObjectId): IO[Throwable, DeleteResult]
}

object CustomerRepository {
  lazy val live: ZLayer[ApplicationDatabase, Nothing, CustomerRepository] = ZLayer {
    for {
      collection <- ZIO.service[ApplicationDatabase]
    } yield CustomerRepositoryLive(collection)
  }
}
