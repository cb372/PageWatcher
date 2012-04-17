package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB

import play.api.Play.current

/**
 * Author: chris
 * Created: 4/17/12
 */

case class EmailSettings(smtpHost: String = "smtp.gmail.com",
                         smtpPort: Int = 587,
                         fromAddress: String = "me@example.com",
                         useTLS: Boolean = true,
                         username: Option[String] = None,
                         password: Option[String] = None)

case class AllSettings(email: EmailSettings)

object AllSettings {
  val email =
    get[String]("email_smtp_host") ~
      get[Int]("email_smtp_port") ~
      get[String]("email_from_address") ~
      get[Boolean]("email_use_tls") ~
      get[Option[String]]("email_smtp_username") ~
      get[Option[String]]("email_smtp_password") map {
      case host ~ port ~ fromAddress ~ useTLS ~ username ~ password =>
      EmailSettings(host, port, fromAddress, useTLS, username, password)
    }
  val allSettings = email map {
    case email => AllSettings(email)
  }

  def load(): AllSettings = DB.withConnection { implicit c =>
    SQL("select * from settings").as(allSettings *)
      .headOption
      .getOrElse(AllSettings(EmailSettings()))
  }

  def save(settings: AllSettings) = DB.withConnection { implicit c =>
    // delete old settings, if any
    SQL("delete from settings").executeUpdate()

    // insert new settings
    SQL("""insert into settings(
            email_smtp_host, email_smtp_port, email_from_address,
            email_use_tls, email_smtp_username, email_smtp_password)
         values ({host}, {port}, {fromAddress}, {useTLS}, {username}, {password})""")
      .on(
          'host -> settings.email.smtpHost,
          'port -> settings.email.smtpPort,
          'fromAddress -> settings.email.fromAddress,
          'useTLS -> settings.email.useTLS,
          'username -> settings.email.username,
          'password -> settings.email.password)
      .executeUpdate()
  }
}