package models.impl

import javax.inject.{Singleton, Inject}

import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future


@Singleton
class DAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile]  {
  import driver.api._
  import scala.concurrent.ExecutionContext.Implicits.global

  private class Users(tag: Tag) extends Table[(String, String)](tag, "USERS") {
    def providerKey = column[String]("ID", O.PrimaryKey)
    def name = column[String]("NAME")

    def * = (providerKey, name)
  }

  private val users = TableQuery[Users]

  db.run(users.schema.create)

  def saveUser(user: User): Future[Unit] = {
    db.run(users.insertOrUpdate((user.providerKey, user.name)))
      .map { _ => () }
  }

  def findUser(providerKey: String): Future[Option[User]] = {
    val query = for (u <- users if (u.providerKey === providerKey)) yield (u.providerKey, u.name)
    db.run(query.take(1).result).map { seq =>
      seq.headOption.map {
        case (providerKey, name) => User(providerKey, name)
      }
    }
  }
}
