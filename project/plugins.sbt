resolvers += "HMRC-open-artefacts-maven" at "https://open.artefacts.tax.service.gov.uk/maven2"
resolvers += Resolver.url("HMRC-open-artefacts-ivy", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(Resolver.ivyStylePatterns)

addSbtPlugin("uk.gov.hmrc" % "sbt-auto-build" % "3.6.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.9.3")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.2")

addSbtPlugin("uk.gov.hmrc" % "sbt-play-cross-compilation" % "2.3.0")

val playPlugin =
  if (sys.env.get("PLAY_VERSION").contains("2.8"))
    "com.typesafe.play" % "sbt-plugin" % "2.8.11"
  else if (sys.env.get("PLAY_VERSION").contains("2.7"))
    "com.typesafe.play" % "sbt-plugin" % "2.7.7"
  else
    "com.typesafe.play" % "sbt-plugin" % "2.6.25"

addSbtPlugin(playPlugin)