import sbt.Resolver
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion
import SCoverageSettings.*
import sbt.Keys.resolvers

ThisBuild / majorVersion     := 3
ThisBuild / isPublicArtefact := true
ThisBuild / scalaVersion     := "3.3.6"

lazy val root = (project in file("."))
  .settings(
    name := "agent-mtd-identifiers",
    libraryDependencies ++= LibDependencies.compile ++ LibDependencies.test,
    scoverageSettings,
    resolvers ++= Seq (
      Resolver.typesafeRepo("releases")
    )
  )
  .disablePlugins(JUnitXmlReportPlugin)
