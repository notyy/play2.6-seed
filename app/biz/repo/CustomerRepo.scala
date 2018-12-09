package biz.repo

import biz.domain.Customer
import slick.basic.DatabasePublisher
import slick.jdbc.{ResultSetConcurrency, ResultSetType}

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

  def customerStream: DatabasePublisher[Customer] = {
    database.runStream(customers.result.withStatementParameters(
      rsType = ResultSetType.ForwardOnly,
      rsConcurrency = ResultSetConcurrency.ReadOnly,
      fetchSize = 10000
    ))
  }
}
