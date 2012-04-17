package controllers


import play.api.data._
import play.api.data.Forms._
import play.api.data.validation._
import play.api.mvc._
import models._

object Settings extends Controller {

  val settingsForm: Form[AllSettings] = Form(
    mapping(
      "email" -> mapping(
        "smtpHost" -> nonEmptyText,
        "smtpPort" -> number (min=1, max=65535),
        "fromAddress" -> email,
        "useTLS" -> boolean,
        "username" -> optional (text),
        "password" -> optional (text)
      )(EmailSettings.apply)(EmailSettings.unapply)
    )(AllSettings.apply)(AllSettings.unapply)
  )

  def show = Action {
    val settings = AllSettings.load()
    Ok(views.html.showSettings(settings))
  }

  def edit = Action { implicit request =>
    val form = settingsForm.fill(AllSettings(EmailSettings()))
    Ok(views.html.editSettings(form))
  }

  def update = TODO

  def testEmail = TODO

}