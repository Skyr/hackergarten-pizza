package lib.silhouette

import com.mohiva.play.silhouette.api.util.HTTPLayer
import com.mohiva.play.silhouette.impl.providers._
import play.api.libs.json.JsValue


object MeetupProvider {
  val ID = "meetup"
  val API = "http://whats.this.for/?"
}


trait BaseMeetupProvider extends OAuth2Provider {
  override type Content = JsValue

  override val id = MeetupProvider.ID

  override protected val urls = Map("api" -> MeetupProvider.API)

  override protected def buildProfile(authInfo: OAuth2Info) = ???
}


class MeetupProfileParser extends SocialProfileParser[JsValue, CommonSocialProfile] {
  override def parse(content: JsValue) = ???
}


class MeetupProvider(protected val httpLayer: HTTPLayer,
                     protected val stateProvider: OAuth2StateProvider,
                     val settings: OAuth2Settings)
  extends BaseMeetupProvider with CommonSocialProfileBuilder {

  type Self = MeetupProvider
  val profileParser = new MeetupProfileParser

  override def withSettings(f: (OAuth2Settings) => OAuth2Settings) = ???
}
