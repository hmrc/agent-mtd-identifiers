import sbt.Resolver
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion
import SCoverageSettings.*
import sbt.Keys.resolvers

val scala2_13 = "2.13.12"

ThisBuild / majorVersion     := 2
ThisBuild / isPublicArtefact := true
ThisBuild / scalaVersion     := scala2_13

lazy val root = (project in file("."))
  .settings(
    name := "agent-mtd-identifiers",
    libraryDependencies ++=LibDependencies.compile ++ LibDependencies.test,
    scoverageSettings,
    resolvers ++= Seq (
      Resolver.typesafeRepo("releases")
    )
  )
  .disablePlugins(JUnitXmlReportPlugin)
