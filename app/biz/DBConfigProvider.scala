package biz

import slick.basic.DatabaseConfig
import slick.jdbc.{H2Profile, JdbcProfile, OracleProfile}

trait DBConfigProvider {
  def getDatabaseConfig(dbName: String): DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig(dbName)
}

//trait OracleDB extends DBConfigProvider {
//  val jdbcProfile: JdbcProfile = OracleProfile
//}
//
//trait H2MemDB extends DBConfigProvider {
//  val jdbcProfile: JdbcProfile = H2Profile
//  override val db: jdbcProfile.backend.DatabaseDef = jdbcProfile.api.Database.forConfig("h2mem1")
//}
//
//trait H2FileDB extends DBConfigProvider {
//  val jdbcProfile: JdbcProfile = H2Profile
//  override val db: jdbcProfile.backend.DatabaseDef = jdbcProfile.api.Database.forConfig("h2file")
//}