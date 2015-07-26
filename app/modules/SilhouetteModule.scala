package modules

import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.api.{Environment, EventBus}
import com.mohiva.play.silhouette.impl.authenticators.{SessionAuthenticator, SessionAuthenticatorService, SessionAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.providers.oauth2.GoogleProvider
import com.mohiva.play.silhouette.impl.providers.oauth2.state.{CookieStateProvider, CookieStateSettings}
import com.mohiva.play.silhouette.impl.providers.{OAuth2Settings, OAuth2StateProvider, SocialProviderRegistry}
import com.mohiva.play.silhouette.impl.util.{SecureRandomIDGenerator, DefaultFingerprintGenerator}
import lib.silhouette.MeetupProvider
import models.{User, UserService}
import models.impl.UserServiceImpl
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.libs.ws.WSClient


class SilhouetteModule extends AbstractModule with ScalaModule {
  import scala.concurrent.ExecutionContext.Implicits.global

  override def configure() = {
    bind[UserService].to[UserServiceImpl]
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())
  }

  @Provides
  def provideEnvironment(userService: UserService,
                         authenticatorService: AuthenticatorService[SessionAuthenticator],
                         eventBus: EventBus): Environment[User, SessionAuthenticator] = {

    Environment[User, SessionAuthenticator](
      userService,
      authenticatorService,
      Seq(),
      eventBus
    )
  }

  @Provides
  def provideAuthenticatorService(configuration: Configuration,
                                  fingerprintGenerator: FingerprintGenerator,
                                  clock: Clock): AuthenticatorService[SessionAuthenticator] = {
    import net.ceedubs.ficus.Ficus._
    import net.ceedubs.ficus.readers.ArbitraryTypeReader._

    val settings = configuration.underlying.as[SessionAuthenticatorSettings]("silhouette.authenticator")
    new SessionAuthenticatorService(settings, fingerprintGenerator, clock)
  }

  @Provides
  def provideSocialProviderRegistry(meetupProvider: MeetupProvider): SocialProviderRegistry = {
    SocialProviderRegistry(Seq(meetupProvider))
  }

  @Provides
  def provideHTTPLayer(client: WSClient): HTTPLayer = new PlayHTTPLayer(client)

  @Provides
  def provideOAuth2StateProvider(idGenerator: IDGenerator, configuration: Configuration, clock: Clock): OAuth2StateProvider = {
    import net.ceedubs.ficus.Ficus._
    import net.ceedubs.ficus.readers.ArbitraryTypeReader._

    val settings = configuration.underlying.as[CookieStateSettings]("silhouette.oauth2StateProvider")
    new CookieStateProvider(settings, idGenerator, clock)
  }

  @Provides
  def provideMeetupProvider(httpLayer: HTTPLayer,
                            stateProvider: OAuth2StateProvider,
                            configuration: Configuration): MeetupProvider = {
    import net.ceedubs.ficus.Ficus._
    import net.ceedubs.ficus.readers.ArbitraryTypeReader._

    new MeetupProvider(httpLayer, stateProvider, configuration.underlying.as[OAuth2Settings]("silhouette.meetup"))
  }
}
