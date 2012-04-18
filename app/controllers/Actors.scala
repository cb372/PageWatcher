package controllers

import akka.actor.Actor
import models.{EmailSettings, Email}
import controllers.Messages.{SendEmailResponse, SendEmail}
import org.apache.commons.mail.{EmailException, DefaultAuthenticator, SimpleEmail}

/**
 *
 * Created: 12/04/18 11:11
 * @author chris
 */

object Actors {

  class EmailSender extends Actor {

    protected def receive = {
      case SendEmail(email, settings) => {
        val mail = new SimpleEmail();
        mail.setHostName(settings.smtpHost)
        mail.setSmtpPort(settings.smtpPort)
        mail.setAuthenticator(new DefaultAuthenticator(
          settings.username.getOrElse(""),
          settings.password.getOrElse("")
        ))
        mail.setTLS(settings.useTLS)
        mail.setFrom(settings.fromAddress)
        mail.setSubject(email.subject.getOrElse(""))
        mail.setMsg(email.body.getOrElse(""))
        email.toAddress.split(""",\s?""") map { addr =>
          mail.addTo(addr)
        }
        try {
          mail.send()
          sender ! SendEmailResponse(true)
        } catch {
          case e: EmailException =>
            sender ! SendEmailResponse(false)
        }
      }
    }
  }
}

object Messages {
  sealed trait Msg
  case class SendEmail(email: Email, settings: EmailSettings) extends Msg
  case class SendEmailResponse(success: Boolean) extends Msg
}
