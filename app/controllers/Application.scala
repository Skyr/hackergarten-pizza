package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import models.User
import play.api._
import play.api.i18n.MessagesApi
import play.api.mvc._

import scala.concurrent.Future


class Application @Inject() (val messagesApi : MessagesApi,
                             val env: Environment[User, SessionAuthenticator],
                             socialProviderRegistry: SocialProviderRegistry)
  extends Silhouette[User, SessionAuthenticator] {

  def index = SecuredAction.async {
    Future.successful(Ok(views.html.index()))
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
