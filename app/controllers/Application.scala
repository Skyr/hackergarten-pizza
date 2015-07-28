package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{LoginInfo, Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.impl.providers.{OAuth2Info, SocialProviderRegistry}
import models.User
import play.api._
import play.api.i18n.MessagesApi
import play.api.mvc._

import scala.concurrent.Future


class Application @Inject() (val messagesApi : MessagesApi,
                             val env: Environment[User, SessionAuthenticator],
                             socialProviderRegistry: SocialProviderRegistry,
                             authInfoDao: DelegableAuthInfoDAO[OAuth2Info])
  extends Silhouette[User, SessionAuthenticator] {
  import scala.concurrent.ExecutionContext.Implicits.global

  def index = SecuredAction.async { implicit request =>
    val loginInfo = LoginInfo("meetup", request.identity.providerKey)
    authInfoDao.find(loginInfo).map { token =>
      Ok(views.html.index(request.identity, token))
    }
    //Future.successful(Ok(views.html.index(request.identity)))
  }

  def login = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(_) => Future.successful(Redirect(routes.Application.index))
      case None => Future.successful(Ok(views.html.login(socialProviderRegistry)))
    }
  }

  override protected def onNotAuthenticated(request: RequestHeader) =
    Some(Future.successful(Redirect(routes.Application.login)))
}
