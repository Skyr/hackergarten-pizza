package models.impl

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.impl.providers.OAuth2Info

import scala.concurrent.Future


class Oauth2InfoStorage @Inject()(db: DAO) extends DelegableAuthInfoDAO[OAuth2Info] {
  import scala.concurrent.ExecutionContext.Implicits.global

  override def find(loginInfo: LoginInfo): Future[Option[OAuth2Info]] =
    db.getOAuthInfo(loginInfo.providerKey)

  override def update(loginInfo: LoginInfo, authInfo: OAuth2Info) =
    db.putOAuthInfo(loginInfo.providerKey, authInfo).map(_ => authInfo)

  override def remove(loginInfo: LoginInfo) =
    db.deleteOAuthInfo(loginInfo.providerKey)

  override def save(loginInfo: LoginInfo, authInfo: OAuth2Info) =
    db.putOAuthInfo(loginInfo.providerKey, authInfo).map(_ => authInfo)

  override def add(loginInfo: LoginInfo, authInfo: OAuth2Info) =
    db.putOAuthInfo(loginInfo.providerKey, authInfo).map(_ => authInfo)
}
