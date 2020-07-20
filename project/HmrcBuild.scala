import sbt.Keys._
import sbt._

import uk.gov.hmrc.playcrosscompilation.AbstractPlayCrossCompilation
import uk.gov.hmrc.playcrosscompilation.PlayVersion.Play25

object PlayCrossCompilation extends AbstractPlayCrossCompilation(defaultPlayVersion = Play25)

object HmrcBuild extends Build {

  import uk.gov.hmrc.SbtArtifactory.autoImport.makePublicallyAvailableOnBintray
  import uk.gov.hmrc.{SbtArtifactory, SbtAutoBuildPlugin}
  import uk.gov.hmrc.versioning.SbtGitVersioning
  import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion


  val appName = "agent-mtd-identifiers"

  lazy val scoverageSettings = {
    import scoverage.ScoverageKeys
    Seq(
      // Semicolon-separated list of regexs matching classes to exclude
      ScoverageKeys.coverageExcludedPackages := """uk\.gov\.hmrc\.BuildInfo;.*\.Routes;.*\.RoutesPrefix;.*\.Reverse[^.]*""",
      ScoverageKeys.coverageMinimum := 80.00,
      ScoverageKeys.coverageFailOnMinimum := false,
      ScoverageKeys.coverageHighlighting := true,
      parallelExecution in Test := false
    )
  }

  lazy val microservice = Project(appName, file("."))
    .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, SbtArtifactory)
    .settings(majorVersion := 0)
    .settings(scoverageSettings: _*)
    .settings(
      scalaVersion := "2.11.12",
      libraryDependencies ++= PlayCrossCompilation.dependencies(
        shared = Seq(
          "org.scalatest"     %% "scalatest"  % "3.0.6"  % Test,
          "org.pegdown"       %  "pegdown"    % "1.6.0"  % Test,
          "org.scalacheck"    %% "scalacheck" % "1.14.0" % Test
        ),
        play25 = Seq(
          "com.typesafe.play" %% "play-json"  % "2.5.19",
          "uk.gov.hmrc" %% "domain" % "5.9.0-play-25"
        ),
        play26 = Seq(
          "com.typesafe.play" %% "play-json"  % "2.6.13",
          "uk.gov.hmrc" %% "domain" % "5.9.0-play-26"
        ),
        play27 = Seq(
          "com.typesafe.play" %% "play-json"  % "2.7.4",
          "uk.gov.hmrc" %% "domain" % "5.9.0-play-27"
        )
      ),
      crossScalaVersions := List("2.11.12", "2.12.8"),
      resolvers := Seq(
        Resolver.bintrayRepo("hmrc", "releases"),
        "typesafe-releases" at "http://repo.typesafe.com/typesafe/releases/"
      )
    ).settings(makePublicallyAvailableOnBintray := true)
    .settings(PlayCrossCompilation.playCrossCompilationSettings)
}