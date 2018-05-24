package biz

import biz.domain.Customer

import scala.concurrent.Await
import scala.concurrent.duration._

trait Database {
  this: DBConfigProvider =>

  def dbName: String

  val dbConfig = getDatabaseConfig(dbName)

  lazy val databaseApi = dbConfig.profile.api

  import dbConfig.profile.api._

  def run[T](action: slick.dbio.DBIOAction[T, NoStream, Nothing]): T = {
    Await.result(runAsync(action), 30 seconds)
  }

  private def runAsync[T](action: DBIOAction[T, databaseApi.NoStream, Nothing]) = {
    dbConfig.db.run(action)
  }

  class CustomerTable(tag: Tag) extends Table[Customer](tag, "customer") {
    def id = column[Option[String]]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")

    def birthday = column[Option[String]]("BIRTHDAY")

    def address = column[Option[String]]("ADDRESS")

    def * = (id, name, birthday, address) <> (Customer.tupled, Customer.unapply)
  }

  val customers = TableQuery[CustomerTable]

  def setupDB(): Unit = {
    println("create tables:")
    customers.schema.createStatements.foreach(println)
    val setUp = DBIO.seq(
      customers.schema.create
    )
    run(setUp)
  }

  def dropDB(): Unit = {
    println("delete tables:")
    val drop = DBIO.seq(
      customers.schema.drop
    )
    run(drop)
  }

  def close(): Unit  = dbConfig.db.close()
}
