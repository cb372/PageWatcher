import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "PageWatcher"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "org.apache.commons" % "commons-email" % "1.2"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
