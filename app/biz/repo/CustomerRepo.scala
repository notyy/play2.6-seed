package biz.repo

import biz.Database
import biz.domain.Customer

trait CustomerRepo {
  val database: Database

  import database.customers
  import database.databaseApi._

  def register(customer: Customer): Customer = {
    val q = (customers returning customers.map(_.id)
      into ((customer, id) => customer.copy(id = id))
      ) += customer
    database.run(q)
  }

  def listAll: Seq[Customer] = database.run(customers.result)
}
