package models.impl

import javax.inject.{Singleton, Inject}

import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future


@Singleton
class DAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile]  {
  import driver.api._
  import scala.pickling.Defaults._
  import scala.pickling.binary._
  import scala.concurrent.ExecutionContext.Implicits.global

  private class Users(tag: Tag) extends Table[(String, String)](tag, "USERS") {
    def providerKey = column[String]("ID", O.PrimaryKey)
    def name = column[String]("NAME")

    def * = (providerKey, name)
  }

  private val users = TableQuery[Users]

  private class OAuth2Info(tag: Tag) extends Table[(String, Array[Byte])](tag, "OAUTH2INFO") {
    def providerKey = column[String]("ID", O.PrimaryKey)
    def data= column[Array[Byte]]("DATA")

    def * = (providerKey, data)
  }

  private val oauth2info = TableQuery[OAuth2Info]


  db.run(users.schema.create)
  db.run(oauth2info.schema.create)


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


  def getOAuthInfo(providerKey: String): Future[Option[com.mohiva.play.silhouette.impl.providers.OAuth2Info]] = {
    val query = for (i <- oauth2info if (i.providerKey === providerKey)) yield i.data
    db.run(query.take(1).result).map { seq =>
      seq.headOption.map {
        case data => data.unpickle[com.mohiva.play.silhouette.impl.providers.OAuth2Info]
      }
    }
  }


  def putOAuthInfo(providerKey: String, oauthInfo: com.mohiva.play.silhouette.impl.providers.OAuth2Info): Future[Unit] = {
    db.run(oauth2info.insertOrUpdate(providerKey, oauthInfo.pickle.value))
      .map { _ => () }
  }


  def deleteOAuthInfo(providerKey: String): Future[Unit] = {
    val query = oauth2info.filter(_.providerKey === providerKey)
    db.run(query.delete)
      .map { _ => () }
  }
}
