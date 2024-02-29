import sbt.Test
import sbt.Keys.parallelExecution
object SCoverageSettings {

  lazy val scoverageSettings = {
    import scoverage.ScoverageKeys
    Seq( // Semicolon-separated list of regexs matching classes to exclude
      ScoverageKeys.coverageExcludedPackages := """uk\.gov\.hmrc\.BuildInfo;.*\.Routes;.*\.RoutesPrefix;.*\.Reverse[^.]*""",
      ScoverageKeys.coverageMinimumStmtTotal := 80.00,
      ScoverageKeys.coverageFailOnMinimum := false,
      ScoverageKeys.coverageHighlighting := true,
      (Test / parallelExecution) := false
    )
  }

}
