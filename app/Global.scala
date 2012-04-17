import play.api.{Application, GlobalSettings}

/**
 * Author: chris
 * Created: 4/17/12
 */

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    println("TODO: Start scheduled actor")
  }

  override def onStop(app: Application) {
    println("TODO: Stop scheduled actor")
  }
}


