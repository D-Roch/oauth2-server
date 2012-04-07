package io.backchat.oauth2

import akka.actor._
import model._
import net.liftweb.json.Formats
import service.SmtpTransport

object OAuth2Extension extends ExtensionId[OAuth2Extension] with ExtensionIdProvider {
  def lookup() = OAuth2Extension

  def createExtension(system: ExtendedActorSystem) = new OAuth2Extension(system)

  val Production = "production"
  val Development = "development"
  val Staging = "staging"
  val Test = "test"

  private def readEnvironmentKey(failWith: String ⇒ Unit = _ ⇒ null) = {
    (sys.env.get("AKKA_MODE") orElse sys.props.get("akka.mode")) getOrElse {
      val inferred = "development"
      failWith("no environment found, defaulting to: " + inferred)
      inferred
    }
  }

  val environment = readEnvironmentKey(System.err.println _)

}

class OAuth2Extension(system: ExtendedActorSystem) extends Extension {

  import JettyMain.confKey
  import OAuth2Extension.{ Production, Development, Staging, Test }

  def environment = OAuth2Extension.environment

  def isProduction = isEnvironment(Production)
  def isDevelopment = isEnvironment(Development)
  def isStaging = isEnvironment(Staging)
  def isTest = isEnvironment(Test)
  def isEnvironment(env: String) = environment equalsIgnoreCase env

  private[this] val cfg = system.settings.config
  private[this] def key(value: String) = confKey("mongo.%s" format value)

  val mongo = MongoConfiguration(
    cfg.getString(key("host")),
    cfg.getInt(key("port")),
    cfg.getString(key("database")),
    cfg.getString(key("user")).blankOption,
    cfg.getString(key("password")).blankOption)

  val defaultFormats: Formats = new OAuth2Formats

  lazy val userProvider = new ResourceOwnerDao(mongo.db("resource_owners"))(system)

  val smtp = new SmtpTransport(SmtpConfig(
    cfg.getString("backchat.smtp.host"),
    cfg.getInt("backchat.smtp.port"),
    MailSender(cfg.getString("backchat.smtp.from.name"), cfg.getString("backchat.smtp.from.email")),
    cfg.getString("backchat.smtp.user").blankOption,
    cfg.getString("backchat.smtp.password").blankOption,
    cfg.getBoolean("backchat.smtp.sslRequired")))

  val web = WebConfig(
    cfg.getString(confKey("web.guiUrl")),
    cfg.getString(confKey("web.host")),
    cfg.getInt(confKey("web.port")),
    cfg.getBoolean(confKey("web.sslRequired")),
    cfg.getString(confKey("web.public")))

}