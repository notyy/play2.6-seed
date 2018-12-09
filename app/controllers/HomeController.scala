package controllers

import akka.stream.scaladsl.{Concat, Source}
import akka.util.ByteString
import biz.repo.{CustomerRepo, DBConfigProvider, Database}
import com.typesafe.scalalogging.StrictLogging
import javax.inject._
import play.api.Logger
import play.api.http.HttpEntity
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with StrictLogging {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def hello() = Action { implicit request: Request[AnyContent] =>
    Ok("""{"result": "hello"}""")
  }

  def download() = Action { implicit request: Request[AnyContent] =>
    val customerRepo: CustomerRepo = prepareCustomerRepo

    showMemoryUsage("before reading data from db")

    val customers = customerRepo.listAll.map(_.toString).mkString("\n")
//    h2fileDB.close()
    showMemoryUsage("after reading data from db")
    val rs = Result(
      header = ResponseHeader(200, Map.empty),
      body = HttpEntity.Strict(ByteString(customers), Some("application/csv"))
    )
    showMemoryUsage("after http result is composed")
    rs
  }

  private def prepareCustomerRepo = {
    val h2fileDB = new Database with DBConfigProvider {
      override def dbName: String = "h2file"
    }

    val customerRepo = new CustomerRepo {
      override val database = h2fileDB
    }
    customerRepo
  }

  def chunked() = Action { implicit request: Request[AnyContent] =>
    val customerRepo: CustomerRepo = prepareCustomerRepo

    showMemoryUsage("before reading data from db")

    val headerSource = Source.single(ByteString(""""id","name","birthday","address"""" + "\n"))
    val customerCsvSource = Source.fromPublisher(customerRepo.customerStream)
      .map(data => ByteString(s""""${data.id}","${data.name}","${data.birthday}","${data.address}"""" + "\n"))
    val csvSource = Source.combine(headerSource, customerCsvSource)(Concat[ByteString])
    showMemoryUsage("after reading data from db")
    val rs = Result(
      header = ResponseHeader(OK, Map(CONTENT_DISPOSITION → s"attachment; filename=customers.csv")),
      body = HttpEntity.Streamed(csvSource, None, None)
    )
    showMemoryUsage("after http result is composed")
    rs
  }

  private def showMemoryUsage(msg: String) = {
    logger.info(msg)
    Runtime.getRuntime.gc()
    logger.info(s"max memory(MB): ${Runtime.getRuntime.maxMemory() / 1024 / 1024}")
    val totalMemory = Runtime.getRuntime.totalMemory() / 1024 / 1024
    logger.info(s"total memory(MB): $totalMemory")
    val freeMemory = Runtime.getRuntime.freeMemory() / 1024 / 1024
    logger.info(s"free memory(MB): $freeMemory")
    logger.info(s"used memory(MB): ${totalMemory - freeMemory}")
  }
}
