package modules

import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util.{Clock, FingerprintGenerator}
import com.mohiva.play.silhouette.api.{Environment, EventBus}
import com.mohiva.play.silhouette.impl.authenticators.{SessionAuthenticator, SessionAuthenticatorService, SessionAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import com.mohiva.play.silhouette.impl.util.DefaultFingerprintGenerator
import models.{User, UserService}
import models.impl.UserServiceImpl
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration


class SilhouetteModule extends AbstractModule with ScalaModule {
  import scala.concurrent.ExecutionContext.Implicits.global

  override def configure() = {
    bind[UserService].to[UserServiceImpl]
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
  def provideSocialProviderRegistry(): SocialProviderRegistry = {
    SocialProviderRegistry(Seq())
  }
}
