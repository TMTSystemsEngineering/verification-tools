import sbt.Keys._
import sbt.{Compile, Global, Setting, Test, _}

object Common {
  lazy val CommonSettings: Seq[Setting[_]] = Seq(
    organizationName := "TMT Org",
    scalaVersion := Libs.ScalaVersion,
    resolvers ++= Seq(
      "jitpack" at "https://jitpack.io"
    ),
    scalacOptions ++= Seq(
      "-encoding",
      "UTF-8",
      "-feature",
      "-unchecked",
      "-deprecation",
      //-W Options
      "-Wdead-code",
      "-Wconf:any:warning-verbose",
      //-X Options
      "-Xlint:_,-missing-interpolator",
      "-Xsource:3",
      "-Xcheckinit",
      "-Xasync"
      // -Y options are rarely needed, please look for -W equivalents
    ),
    javacOptions in (Compile, doc) ++= Seq("-Xdoclint:none"),
    javacOptions in doc ++= Seq("--ignore-source-errors"),
    publishArtifact in (Test, packageBin) := true,
    version := "0.1",
    fork := true,
    javaOptions in Test ++= Seq("-Dakka.actor.serialize-messages=on"),
    autoCompilerPlugins := true,
    cancelable in Global := true // allow ongoing test(or any task) to cancel with ctrl + c and still remain inside sbt
  )
}