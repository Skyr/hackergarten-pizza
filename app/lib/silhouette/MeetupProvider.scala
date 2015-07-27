package lib.silhouette

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.HTTPLayer
import com.mohiva.play.silhouette.impl.exceptions.ProfileRetrievalException
import com.mohiva.play.silhouette.impl.providers._
import play.api.Logger
import play.api.libs.json.JsValue
import play.api.http.HeaderNames._

import scala.concurrent.Future


object MeetupProvider {
  val ID = "meetup"
  val API = "https://api.meetup.com/2/member/self?&sign=true"
}


trait BaseMeetupProvider extends OAuth2Provider {
  override type Content = JsValue

  override val id = MeetupProvider.ID

  override protected val urls = Map("api" -> MeetupProvider.API)

  override protected def buildProfile(authInfo: OAuth2Info): Future[Profile] = {
    httpLayer.url(urls("api")).withHeaders(AUTHORIZATION -> s"Bearer ${authInfo.accessToken}").get().flatMap { response =>
      response.status match {
        case 200 => profileParser.parse(response.json)
        case _ => throw new ProfileRetrievalException(response.statusText)
      }
    }
  }
}


class MeetupProfileParser extends SocialProfileParser[JsValue, CommonSocialProfile] {
  override def parse(content: JsValue) = Future.successful {
    CommonSocialProfile(
      loginInfo = LoginInfo(MeetupProvider.ID, (content \ "id").as[Int].toString),
      fullName = (content \ "name").asOpt[String],
      avatarURL = (content \ "photo" \ "thumb_link").asOpt[String]
    )
  }
}


class MeetupProvider(protected val httpLayer: HTTPLayer,
                     protected val stateProvider: OAuth2StateProvider,
                     val settings: OAuth2Settings)
  extends BaseMeetupProvider with CommonSocialProfileBuilder {

  type Self = MeetupProvider
  val profileParser = new MeetupProfileParser

  override def withSettings(f: (Settings) => Settings) = new MeetupProvider(httpLayer, stateProvider, f(settings))
}
