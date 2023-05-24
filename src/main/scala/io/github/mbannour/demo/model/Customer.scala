package io.github.mbannour.demo.model

import scala.util.Try

import org.bson.types.ObjectId
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class Customer(
    _id: ObjectId,
    name: String,
    age: Int,
    email: String,
    phoneNumber: String,
    phoneWork: Option[String],
    address: Address,
  )

case class Address(
    street: String,
    houseNumber: String,
    code: Int,
  )

object Customer {
  def apply(
      name: String,
      age: Int,
      email: String,
      phoneNumber: String,
      workNumber: Option[String],
      address: Address,
    ): Customer = Customer(new ObjectId(), name, age, email, phoneNumber, workNumber, address)

  object FieldNames {
    def name        = "name"
    def age         = "age"
    def id          = "_id"
    def email       = "email"
    def phoneWork   = "phoneWork"
    def phoneNumber = "phoneNumber"
    def street      = "address.street"
    def houseNumber = "address.houseNumber"
    def code        = "address.code"
  }

  implicit val objectIdEncoder: JsonEncoder[ObjectId] =
    JsonEncoder[String].contramap(_.toHexString)
  implicit val objectIdDecoder: JsonDecoder[ObjectId] =
    JsonDecoder[String].mapOrFail { string =>
      Try(new ObjectId(string)).toEither match {
        case Right(r) => Right(r)
        case Left(_)  => Left(s"Invalid ObjectId: '$string'")
      }
    }

  implicit val addressEncoder: JsonEncoder[Address] = DeriveJsonEncoder.gen[Address]

  implicit val customerEncoder: JsonEncoder[Customer] = DeriveJsonEncoder.gen[Customer]
}
