package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Silhouette, Environment}
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.impl.providers.{OAuth2Info, SocialProviderRegistry}
import models.User
import models.pizza.PizzaRepo
import play.api.i18n.MessagesApi
import play.api.mvc._

import scala.concurrent.Future


class Admin @Inject() (val messagesApi : MessagesApi,
                       val env: Environment[User, SessionAuthenticator],
                       socialProviderRegistry: SocialProviderRegistry,
                       authInfoDao: DelegableAuthInfoDAO[OAuth2Info])
    extends Silhouette[User, SessionAuthenticator] {

  def index = Action { request =>
    val pizzas = PizzaRepo.pizzas.values.toList.sortBy(_.id)
    Ok(views.html.pizzas(pizzas))
  }
}
