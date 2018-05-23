package biz.domain

final case class Customer(id: Option[String], name: String, birthday: Option[String], address: Option[String])
final case class Product(id: Option[String], name: String)
final case class Trade(id: Option[String], customer: Customer, product: Product)