import sbt.{Resolver}
import uk.gov.hmrc.SbtArtifactory.autoImport.makePublicallyAvailableOnBintray
import uk.gov.hmrc.{SbtArtifactory, SbtAutoBuildPlugin}
import uk.gov.hmrc.versioning.SbtGitVersioning
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion


lazy val scoverageSettings = {
  import scoverage.ScoverageKeys
  Seq(   // Semicolon-separated list of regexs matching classes to exclude
    ScoverageKeys.coverageExcludedPackages := """uk\.gov\.hmrc\.BuildInfo;.*\.Routes;.*\.RoutesPrefix;.*\.Reverse[^.]*""",
    ScoverageKeys.coverageMinimum := 80.00,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true,
    parallelExecution in Test := false
  )
}

val allDependencies = PlayCrossCompilation.dependencies(
  shared = Seq(
    "org.scalatest" %% "scalatest" % "3.0.6" % Test,
    "org.pegdown" % "pegdown" % "1.6.0" % Test,
    "org.scalacheck" %% "scalacheck" % "1.14.0" % Test
  ),
  play25 = Seq(
    "com.typesafe.play" %% "play-json" % "2.5.19",
    "uk.gov.hmrc" %% "domain" % "5.10.0-play-25"
  ),
  play26 = Seq(
    "com.typesafe.play" %% "play-json" % "2.6.13",
    "uk.gov.hmrc" %% "domain" % "5.10.0-play-26"
  ),
  play27 = Seq(
    "com.typesafe.play" %% "play-json" % "2.7.4",
    "uk.gov.hmrc" %% "domain" % "5.10.0-play-27"
  )
)


lazy val root = (project in file("."))
  .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, SbtArtifactory)
  .settings(
    name := "agent-mtd-identifiers",
    organization := "uk.gov.hmrc",
    scalaVersion := "2.12.10",
    crossScalaVersions := List("2.11.12", "2.12.8"),
    majorVersion := 0,
    makePublicallyAvailableOnBintray := true,
    scoverageSettings,
    resolvers := Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      Resolver.bintrayRepo("hmrc", "release-candidates"),
      Resolver.typesafeRepo("releases"),
      Resolver.jcenterRepo
    ),
    libraryDependencies ++= allDependencies
  )
  .settings(PlayCrossCompilation.playCrossCompilationSettings)

