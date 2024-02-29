import sbt._
object LibDependencies {

  val compile = Seq(
    "org.playframework" %% "play-json" % "3.0.2",
    "uk.gov.hmrc" %% s"domain-play-30" % "9.0.0",
  )

  val test = Seq(
    "org.scalatest" %% "scalatest" % "3.2.15" % Test,
    "org.pegdown" % "pegdown" % "1.6.0" % Test,
    "org.scalacheck" %% "scalacheck" % "1.17.0" % Test,
    "com.vladsch.flexmark" % "flexmark-all" % "0.64.0" % Test
  )
}
