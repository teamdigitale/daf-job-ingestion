import sbtsparksubmit.SparkSubmitPlugin.autoImport._

object SparkSubmit {
  lazy val settings =
    SparkSubmitSetting("localIngestion",
      Seq("--class", "it.gov.daf.ingestion.Ingestion"),
      Seq("--master", "local")
    )
}
