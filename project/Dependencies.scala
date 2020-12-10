import sbt._

object Dependencies {
  val `vam-tool` = Seq(
    Akka.`akka-actor-typed`,
    Akka.`akka-stream-typed`,
    Libs.`mockito-scala`         % Test,
    Libs.scalatest               % Test,
    Akka.`akka-stream-testkit`   % Test
  )
}
