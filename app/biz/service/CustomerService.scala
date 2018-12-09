package biz.service

import biz.domain.Customer
import biz.repo.CustomerRepo
import slick.basic.DatabasePublisher

trait CustomerService {
  this: CustomerRepo =>

  def getAllCustomersAsString(): String = {
    listAll.map(_.toString).mkString("\n")
  }

  def customerAsStream():DatabasePublisher[Customer] = customerStream
}
