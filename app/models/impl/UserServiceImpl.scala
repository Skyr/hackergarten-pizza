package models.impl

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import models.{User, UserService}

import scala.concurrent.Future


class UserServiceImpl @Inject()(db: DAO) extends UserService {
  import scala.concurrent.ExecutionContext.Implicits.global

  override def save(profile: CommonSocialProfile): Future[User] = {
    val user = User(profile.loginInfo.providerKey, profile.fullName.getOrElse("?"))
    db.saveUser(user).map(_ => user)
  }

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = db.findUser(loginInfo.providerKey)
}
