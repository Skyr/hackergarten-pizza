package models.impl

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import models.{User, UserService}


class UserServiceImpl extends UserService {
  override def save(profile: CommonSocialProfile) = ???

  override def retrieve(loginInfo: LoginInfo) = ???
}
