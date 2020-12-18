import sbt._

object Libs {
  val ScalaVersion = "2.13.3"

  val `mockito-scala`      = "org.mockito"                    %% "mockito-scala"      % "1.16.0" // MIT License
  val `scala-async`        = "org.scala-lang.modules" %% "scala-async" % "1.0.0-M1" //BSD 3-clause "New" or "Revised" License
  val scalatest            = "org.scalatest" %% "scalatest" % "3.1.4" //Apache License 2.0
}

object Akka {
  private val Version     = "2.6.10"
  val `akka-actor-typed`  = "com.typesafe.akka" %% "akka-actor-typed"  % Version
  val `akka-stream-typed` = "com.typesafe.akka" %% "akka-stream-typed" % Version
  val `akka-stream`       = "com.typesafe.akka" %% "akka-stream"       % Version
  val `akka-remote`       = "com.typesafe.akka" %% "akka-remote"       % Version

  val `akka-actor-testkit-typed` = "com.typesafe.akka" %% "akka-actor-testkit-typed" % Version
  val `akka-stream-testkit`      = "com.typesafe.akka" %% "akka-stream-testkit"      % Version
  val `akka-multi-node-testkit`  = "com.typesafe.akka" %% "akka-multi-node-testkit"  % Version
}

object AkkaHttp {
  val Version             = "10.2.2"
  val `akka-http-testkit` = "com.typesafe.akka" %% "akka-http-testkit" % Version //ApacheV2
  val `akka-http`         = "com.typesafe.akka" %% "akka-http" % Version
  val `spray-json`        = "com.typesafe.akka" %% "akka-http-spray-json" % Version
}
