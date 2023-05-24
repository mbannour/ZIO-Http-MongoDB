package io.github.mbannour.demo.model

import zio.json._

case class CustomerBody(
    name: String,
    age: Int,
    email: String,
    phoneNumber: String,
    phoneWork: Option[String],
    address: Address)

object CustomerBody {

  implicit val addressDecoder: JsonDecoder[Address] = DeriveJsonDecoder.gen[Address]

  implicit val customerDecoder: JsonDecoder[CustomerBody] = DeriveJsonDecoder.gen[CustomerBody]

  implicit val addressEncoder: JsonEncoder[Address] = DeriveJsonEncoder.gen[Address]

  implicit val customerEncoder: JsonEncoder[CustomerBody] = DeriveJsonEncoder.gen[CustomerBody]

}
