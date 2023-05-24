package io.github.mbannour.demo.repository

import com.mongodb.client.model.Filters
import io.github.mbannour.MongoZioCollection
import io.github.mbannour.demo.model.{Customer, CustomerBody}
import io.github.mbannour.demo.mongo.ApplicationDatabase
import io.github.mbannour.result.{DeleteResult, InsertOneResult, UpdateResult}
import org.bson.types.ObjectId
import org.mongodb.scala.model.Updates
import zio.IO
import io.github.mbannour.demo.model.Customer.FieldNames._

case class CustomerRepositoryLive(database: ApplicationDatabase) extends CustomerRepository {
  override def collection: MongoZioCollection[Customer] =
    database.getDatabase.getCollection("customers")

  override def insertCustomer(customer: Customer): IO[Throwable, InsertOneResult] =
    collection.insertOne(customer)

  override def getCustomerById(customerId: ObjectId): IO[Throwable, Option[Customer]] =
    collection.find(Filters.eq("_id", customerId)).first().headOption

  override def updateCustomer(
      customerId: ObjectId,
      customer: CustomerBody,
    ): IO[Throwable, UpdateResult] = collection.updateOne(
    Filters.eq(id, customerId),
    Updates.combine(
      Updates.set(name, customer.name),
      Updates.set(age, customer.age),
      Updates.set(email, customer.email),
      Updates.set(phoneWork, customer.phoneWork),
      Updates.set(street, customer.address.street),
      Updates.set(houseNumber, customer.address.houseNumber),
      Updates.set(code, customer.address.code),
    ),
  )

  override def deleteCustomer(customerId: ObjectId): IO[Throwable, DeleteResult] =
    collection.deleteOne(Filters.eq("_id", customerId))

  override def getCustomers(): IO[Throwable, Iterator[Customer]] =
    collection.find().fetch
}
