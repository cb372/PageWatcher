package controllers


import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import models._

import play.api.libs.concurrent._
import akka.actor._
import akka.pattern._
import akka.util._
import akka.util.Timeout._
import akka.util.duration._
import controllers.Actors.EmailSender
import controllers.Messages.{SendEmailResponse, SendEmail}

import play.api.Play.current

object Settings extends Controller {
  implicit val emailSendTimeout: Timeout = 10.seconds

  val emailSender = Akka.system.actorOf(Props[EmailSender], name = "emailSender")

  val settingsForm: Form[AllSettings] = Form(
    mapping(
      "email" -> mapping(
        "smtpHost" -> nonEmptyText,
        "smtpPort" -> number(min = 1, max = 65535),
        "fromAddress" -> email,
        "useTLS" -> boolean,
        "username" -> optional(text),
        "password" -> optional(text)
      )(EmailSettings.apply)(EmailSettings.unapply)
    )(AllSettings.apply)(AllSettings.unapply)
  )

  val sendEmailForm: Form[Email] = Form(
    mapping(
        "toAddress" -> email,
        "subject" -> optional(text),
        "body" -> optional(text)
    )(Email.apply)(Email.unapply)
  )

  def show = Action { implicit request =>
    val settings = AllSettings.load()
    val form = sendEmailForm.fill(Email())
    Ok(views.html.showSettings(settings, form))
  }

  def edit = Action { implicit request =>
      val form = settingsForm.fill(AllSettings.load())
      Ok(views.html.editSettings(form))
  }

  def update = Action { implicit request =>
    val form = settingsForm.bindFromRequest
    form fold(
      formWithErrors =>
        Ok(views.html.editSettings(form)(
          Flash(Map("error" -> "There was a problem with the settings you entered")))
        ),
      validSettings => {
        AllSettings.save(validSettings)
        Redirect(routes.Settings.show).flashing("info" -> "Successfully updated settings")
      }
    )
  }

  def testEmail = Action { implicit request =>
    val form = sendEmailForm.bindFromRequest
    val settings = AllSettings.load()
    form fold(
      formWithErrors =>
        Ok(views.html.showSettings(settings, formWithErrors)(
          Flash(Map("error" -> "There was a problem with the settings you entered")))
      ),
      email => {
        Async {
          val future = (emailSender ? SendEmail(email, settings.email)).mapTo[SendEmailResponse]
          future.asPromise map { response =>
            val flash =
              if (response.success)
                Flash(Map("info" -> "Successfully sent test email"))
              else
                Flash(Map("error" -> "Failed to send test email"))
            Ok(views.html.showSettings(settings, sendEmailForm.fill(email))(flash))
          }
        }
      }
    )

  }

}