import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "hello-twitter-play"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "org.twitter4j" % "twitter4j-stream" % "2.2.5"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here      
    )

}
