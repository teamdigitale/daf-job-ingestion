import Versions._
import uk.gov.hmrc.gitstamp.GitStampPlugin._

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
    exclude("commons-collections", "commons-collections").
    exclude("commons-beanutils", "commons-beanutils").
    exclude("org.slf4j", "slf4j-log4j12")

def sparkDependencies(scope: String = "provided") = Seq(
  "spark-core",
  "spark-sql"
) map( c => sparkExcludes(spark %% c % sparkVersion % scope))

parallelExecution in Test := false

libraryDependencies ++= sparkDependencies()
// libraryDependencies += "org.typelevel" %% "cats-core" % catsVersion

resolvers += Resolver.sonatypeRepo("releases")

val framelessVersion = "0.4.0"

libraryDependencies ++= List(
  "org.typelevel" %% "frameless-cats"      % framelessVersion,
  "org.typelevel" %% "frameless-dataset"   % framelessVersion,
  "org.typelevel" %% "frameless-ml"      % framelessVersion,
  "com.holdenkarau" %% "spark-testing-base" % "2.2.0_0.8.0" % "test",
  "org.apache.spark" %% "spark-hive"       % "2.2.0" % "test")
