/*
 * Copyright 2017 - 2018 TEAM PER LA TRASFORMAZIONE DIGITALE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.gov.daf.ingestion

// import play.api.libs.ws.WSClient
// import play.api.libs.ws.ahc.AhcWSClient
import monix.execution.Scheduler.Implicits.global
import pureconfig.loadConfig
import com.typesafe.config.ConfigFactory
import cats._,cats.data._
import cats.implicits._
import cats.data.EitherT._
import io.circe.generic.extras._, io.circe.syntax._, io.circe.generic.auto._, io.circe._
import io.circe.parser.decode
import org.apache.spark.sql.SparkSession
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{ DataFrame, Row, SQLContext }
import monix.eval.Task

import it.gov.daf.ingestion.transformations._
import it.gov.daf.ingestion.model._
import it.gov.daf.ingestion.client.CatalogClient
import it.gov.daf.ingestion.client.CatalogCaller
import it.gov.daf.catalog_manager.yaml.MetaCatalog
import it.gov.daf.ingestion.PipelineBuilder._
import client._

object Ingestion {

  implicit val spark = SparkSession
    .builder
    .appName("Ingestion")
    .getOrCreate()

  private def outputUri(uri: String) = {
    val (pre, post) = uri.splitAt(uri.lastIndexOf("/"))
    s"$pre/final$post"
  }

  def transform(data: DataFrame, pipeline: Pipeline): Either[IngestionError, DataFrame] = {
    println(pipeline)
    val allTransformations = getTransformations(ConfigFactory.load)

    val transformations: List[Transformation] =
      rawSaver +:
    codecTransformation(pipeline.encoding) +:
    commonTransformation +:
    pipeline.steps.sortBy(_.priority).map(s =>
      allTransformations(s.name).transform(s.stepDetails))

    println(transformations.length)
    transformations.map(Kleisli(_)).reduceLeft(_.andThen(_)).apply(data)
  }

  def standardize(dsUri: String)(implicit catalog: CatalogBuilder): Ingestion[(DataFrame, String)] = for {
    dataCatalog  <- catalog(dsUri)
    pipeline     <- buildPipeline(dataCatalog)
    data         =  spark.read.parquet(pipeline.datasetPath).cache
    transformed  <- fromEither[Task](transform(data, pipeline))
  } yield (transformed, outputUri(pipeline.datasetPath))

  def main(args: Array[String]) = {

    val sc = spark.sparkContext
    import spark.sqlContext.implicits._

    import monix.eval.Task

    implicit val catalog: CatalogBuilder = CatalogCaller.catalog _

    // TBD This will be the result of conversion from logical to physical URI
    val dsUri = args(0)

    val postalUri: Broadcast[String]  = spark.sparkContext.broadcast(args(1))

    // val data = spark.read.parquet(dsUri).cache

    /* TODO: Remove this
     *  This is only for testing the Kylo integration
     */
    import java.sql.Timestamp
    val data = sc.parallelize(List(
      ("",  Timestamp.valueOf("2015-01-01 00:00:00")),
      ("b1",Timestamp.valueOf("2016-01-01 00:00:00")),
      ("c1",Timestamp.valueOf("2017-01-01 00:00:00"))
    )).toDF("key","value")
    /******************************/

    standardize(dsUri).value.foreach {_.fold(
      l => println(s"left: $l"),
      r => r._1.write.format("parquet").save(r._2)
    )}

    spark.stop()
  }

}
