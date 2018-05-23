import biz.domain.Customer
import biz.repo.CustomerRepo
import biz.{DBConfigProvider, Database}
import com.typesafe.scalalogging.StrictLogging

/**
  * each time this object is run, 10M customer data will
  * be inserted into database, so DON'T run it often
  */
object DBSetup extends App with StrictLogging {
  logger.info("create database")
  val h2fileDB = new Database with DBConfigProvider {
    override def dbName: String = "h2file"
  }
  h2fileDB.dropDB()
  h2fileDB.setupDB()
  logger.info("database created")

  val customerRepo = new CustomerRepo{
    override val database = h2fileDB
  }

  val i = 10000000
  logger.info(s"insert $i data into customer table")
  (1 to 100).foreach { i =>
    val customer = Customer(None, s"yy$i",Some("20180523"),Some("some address on earth"))
    customerRepo.register(customer)
  }
  logger.info(s"$i data inserted")

  logger.info(s"there are ${customerRepo.listAll.size} data in customer table")
  h2fileDB.close()
}
