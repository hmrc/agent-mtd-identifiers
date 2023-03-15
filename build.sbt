import sbt.Resolver
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion


lazy val scoverageSettings = {
  import scoverage.ScoverageKeys
  Seq(   // Semicolon-separated list of regexs matching classes to exclude
    ScoverageKeys.coverageExcludedPackages := """uk\.gov\.hmrc\.BuildInfo;.*\.Routes;.*\.RoutesPrefix;.*\.Reverse[^.]*""",
    ScoverageKeys.coverageMinimumStmtTotal := 80.00,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true,
    Test / parallelExecution := false
  )
}

val allDependencies = Seq(
    "org.mongodb" % "bson" % "4.6.1",
    "org.scalatest" %% "scalatest" % "3.0.6" % Test,
    "org.pegdown" % "pegdown" % "1.6.0" % Test,
    "org.scalacheck" %% "scalacheck" % "1.14.0" % Test,
    "com.typesafe.play" %% "play-json" % "2.9.2",
    "uk.gov.hmrc" %% "domain" % "8.1.0-play-28"
  )

val scala2_12 = "2.12.15"
val scala2_13 = "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "agent-mtd-identifiers",
    organization := "uk.gov.hmrc",
    scalaVersion := scala2_12,
    crossScalaVersions := List(scala2_12, scala2_13),
    majorVersion := 1,
    isPublicArtefact := true,
    scoverageSettings,
    resolvers ++= Seq(
      Resolver.typesafeRepo("releases"),
    ),
    libraryDependencies ++= allDependencies
  )


