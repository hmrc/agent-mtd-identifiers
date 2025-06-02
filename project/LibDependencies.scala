import sbt.*
object LibDependencies {

  val compile: Seq[ModuleID] = Seq(
    "org.playframework" %% "play-json" % "3.0.4",
    "uk.gov.hmrc" %% s"domain-play-30" % "12.1.0",
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    "org.pegdown" % "pegdown" % "1.6.0" % Test,
    "org.scalacheck" %% "scalacheck" % "1.17.0" % Test,
    "com.vladsch.flexmark" % "flexmark-all" % "0.64.0" % Test
  )
}
