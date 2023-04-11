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
    "com.typesafe.play"    %% "play-json"     % "2.9.2",
    "uk.gov.hmrc"          %% "domain"        % "8.2.0-play-28",
    "org.scalatest"        %% "scalatest"     % "3.2.15"         % Test,
    "org.pegdown"          % "pegdown"        % "1.6.0"          % Test,
    "org.scalacheck"       %% "scalacheck"    % "1.17.0"         % Test,
    "com.vladsch.flexmark" %  "flexmark-all"  % "0.62.2"         % Test
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


