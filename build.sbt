import sbt.Resolver
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion


lazy val scoverageSettings = {
  import scoverage.ScoverageKeys
  Seq(   // Semicolon-separated list of regexs matching classes to exclude
    ScoverageKeys.coverageExcludedPackages := """uk\.gov\.hmrc\.BuildInfo;.*\.Routes;.*\.RoutesPrefix;.*\.Reverse[^.]*""",
    ScoverageKeys.coverageMinimum := 80.00,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true,
    Test / parallelExecution := false
  )
}

val allDependencies = PlayCrossCompilation.dependencies(
  shared = Seq(
    "org.scalatest" %% "scalatest" % "3.0.6" % Test,
    "org.pegdown" % "pegdown" % "1.6.0" % Test,
    "org.scalacheck" %% "scalacheck" % "1.14.0" % Test
  ),
  play26 = Seq(
    "com.typesafe.play" %% "play-json" % "2.6.13",
    "uk.gov.hmrc" %% "domain" % "6.0.0-play-26"
  ),
  play27 = Seq(
    "com.typesafe.play" %% "play-json" % "2.7.4",
    "uk.gov.hmrc" %% "domain" % "6.0.0-play-27"
  ),
  play28 = Seq(
    "com.typesafe.play"      %% "play-json"          % "2.8.1",
    "uk.gov.hmrc" %% "domain" % "8.0.0-play-28"
  )
)


lazy val root = (project in file("."))
  .settings(
    name := "agent-mtd-identifiers",
    organization := "uk.gov.hmrc",
    scalaVersion := "2.12.15",
    crossScalaVersions := List("2.12.15"),
    majorVersion := 0,
    isPublicArtefact := true,
    scoverageSettings,
    resolvers ++= Seq(
      Resolver.typesafeRepo("releases"),
    ),
    libraryDependencies ++= allDependencies
  )
  .settings(PlayCrossCompilation.playCrossCompilationSettings)

