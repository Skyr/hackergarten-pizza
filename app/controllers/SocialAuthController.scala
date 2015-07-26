package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import models.User
import play.api.i18n.MessagesApi
import play.api.mvc.Action


class SocialAuthController @Inject() (val messagesApi : MessagesApi,
                                      val env: Environment[User, SessionAuthenticator])
  extends Silhouette[User, SessionAuthenticator] {

  def authenticate(provider: String) = Action.async { implicit request =>
    ???
  }
}
