import Versions._
import de.heikoseeberger.sbtheader.license.Apache2_0
import uk.gov.hmrc.gitstamp.GitStampPlugin._

// SparkSubmit.settings

organization in ThisBuild := "it.gov.daf"
name := "daf-job-ingestion"

Seq(gitStampSettings: _*)

version in ThisBuild := "1.0.0-SNAPSHOT"

// scalacOptions.in(ThisBuild) ~= filterConsoleScalacOptions

fork in Test := true
javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:+CMSClassUnloadingEnabled")

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8", // yes, this is 2 args
  "-feature",
  "-unchecked",
  "-language:postfixOps",
  "-language:implicitConversions",
  // "-Xfatal-warnings",
  // "-Xlint",
  // "-Ylog-classpath",
  "-Yno-adapted-args",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-dead-code",
  "-Xfuture",
  "-Ypartial-unification")

scalaVersion in ThisBuild := "2.11.12"

lazy val root = (project in file(".")).enablePlugins(AutomateHeaderPlugin)

lazy val spark = "org.apache.spark"

val sparkExcludes =
  (moduleId: ModuleID) => moduleId.
    // exclude("org.apache.hadoop", "hadoop-client").
    exclude("org.apache.hadoop", "hadoop-yarn-client").
    exclude("org.apache.hadoop", "hadoop-yarn-api").
    exclude("org.apache.hadoop", "hadoop-yarn-common").
    exclude("org.apache.hadoop", "hadoop-yarn-server-common").
    exclude("org.apache.hadoop", "hadoop-yarn-server-web-proxy").
    exclude("org.apache.zookeeper", "zookeeper").
    exclude("io.netty", "netty").
    // exclude("com.fasterxml.jackson.core", "jackson-databind").
  // exclude("commons-collections", "commons-collections").
    exclude("commons-beanutils", "commons-beanutils")
    // exclude("org.slf4j", "slf4j-log4j12")

def sparkDependencies(scope: String = "provided") = Seq(
  "spark-core",
  "spark-sql"
) map( c => sparkExcludes(spark %% c % sparkVersion % scope))

dependencyOverrides += "com.google.guava" % "guava" % "16.0.1" % "compile"
dependencyOverrides += "org.asynchttpclient" % "async-http-client" % "2.0.38"

lazy val circe = "io.circe"

val circeDependencies = Seq(
  "circe-core",
  "circe-generic-extras",
  "circe-parser"
) map(circe %% _ % circeVersion)

lazy val sttp = "com.softwaremill.sttp"

val sttpDependencies = Seq(
    "core",
    "circe",
    "okhttp-backend",
    "async-http-client-backend-monix"
  ) map(sttp %% _ % sttpVersion)

lazy val monix = "io.monix"

  val monixDependencies = Seq(
    "monix"
  ) map(monix %% _ % monixVersion)

parallelExecution in Test := false


libraryDependencies ++= sttpDependencies
libraryDependencies ++= sparkDependencies()
libraryDependencies ++= monixDependencies
libraryDependencies ++= circeDependencies
// libraryDependencies += "org.typelevel" %% "cats-core" % catsVersion

resolvers ++= Seq(
  Resolver.mavenLocal,
  // "daf repo" at "http://nexus.default.svc.cluster.local:8081/repository/mavens-public/",
  "cloudera" at "https://repository.cloudera.com/artifactory/cloudera-repos/"
    // "daf repo" at "http://nexus.default.svc.cluster.local:8081/repository/maven-public/"
)


libraryDependencies ++= List(
  // "it.gov.daf" %% "daf-catalog-manager-client" % Versions.dafCatalogVersion exclude("com.fasterxml.jackson.core", "jackson-databind"),
  // "it.gov.daf" %% "common" % Versions.dafCommonVersion,
  "com.github.pureconfig" %% "pureconfig" % pureconfigVersion,
  "com.typesafe" % "config" % "1.3.1",
  "org.typelevel" %% "frameless-cats"      % framelessVersion,
  "org.typelevel" %% "frameless-dataset"   % framelessVersion,
  "org.typelevel" %% "frameless-ml"      % framelessVersion,
  "io.netty" % "netty-all" % "4.0.54.Final",
  "com.joestelmach" % "natty" % "0.13",
  "com.holdenkarau" %% "spark-testing-base" % "2.2.0_0.8.0" % "test",
  "org.apache.spark" %% "spark-hive"       % "2.2.0" % "test")

headers := Map(
  "sbt" -> Apache2_0("2017 - 2018", "TEAM PER LA TRASFORMAZIONE DIGITALE"),
  "scala" -> Apache2_0("2017 - 2018", "TEAM PER LA TRASFORMAZIONE DIGITALE"),
  "conf" -> Apache2_0("2017 - 2018", "TEAM PER LA TRASFORMAZIONE DIGITALE", "#"),
  "properties" -> Apache2_0("2017 - 2018", "TEAM PER LA TRASFORMAZIONE DIGITALE", "#"),
  "yaml" -> Apache2_0("2017 - 2018", "TEAM PER LA TRASFORMAZIONE DIGITALE", "#")
)
