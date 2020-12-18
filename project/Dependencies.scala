import sbt._

object Dependencies {
  val `vam-tool` = Seq(
    Akka.`akka-actor-typed`,
    Akka.`akka-stream-typed`,
    Libs.`mockito-scala`         % Test,
    Libs.scalatest               % Test,
    Akka.`akka-stream-testkit`   % Test
  )

  val `jira-updater` = Seq(
    Akka.`akka-actor-typed`,
    Akka.`akka-stream-typed`,
    AkkaHttp.`akka-http`,
    AkkaHttp.`spray-json`,
    Libs.`mockito-scala`         % Test,
    Libs.scalatest               % Test,
    Akka.`akka-stream-testkit`   % Test,
    AkkaHttp.`akka-http-testkit` % Test
  )

}
