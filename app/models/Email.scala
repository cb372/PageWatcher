package models

/**
 *
 * Created: 12/04/18 11:13
 * @author chris
 */

case class Email(toAddress: String = "me@example.com",
                 subject: Option[String] = Some("Test email from PageWatcher"),
                 body: Option[String] = Some("This is a test"))
