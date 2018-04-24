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

import com.typesafe.config.ConfigFactory
import cats._,cats.data._
import cats.implicits._
import io.circe.generic.extras._, io.circe.syntax._, io.circe.generic.auto._, io.circe._
import io.circe.parser.decode
import org.apache.spark.sql.SparkSession
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{ DataFrame, Row, SQLContext }

import it.gov.daf.ingestion.transformations._
import it.gov.daf.ingestion.model._

object Ingestion {

  implicit val spark = SparkSession
    .builder
    .appName("Ingestion")
    .getOrCreate()

  private def outputUri(uri: String) = {
    val (pre, post) = uri.splitAt(uri.lastIndexOf("/"))
    s"$pre/final$post"
  }

  def ingest(data: DataFrame, pipeline: Pipeline): Either[IngestionError, DataFrame] = {

    val allTransformations = getTransformations(ConfigFactory.load)

    val transformations: List[Transformation] =
      rawSaver +: commonTransformation +: pipeline.steps.sortBy(_.priority).map(s => allTransformations(s.name).transform(s.stepDetails))

    transformations.map(Kleisli(_)).reduceLeft(_.andThen(_)).apply(data)
  }

  def main(args: Array[String]) = {

    // TBD This will be the result of conversion from logical to physical URI
    val dsUri = ""

    val postalUri: Broadcast[String]  = spark.sparkContext.broadcast(args(1))

    // val data = spark.read.parquet(dsUri).cache

    /* TODO: Remove this
     *  This is only for testing the Kylo integration
     */
    import java.sql.Timestamp
    val sc = spark.sparkContext
    import spark.sqlContext.implicits._

    val data = sc.parallelize(List(
      ("",  Timestamp.valueOf("2015-01-01 00:00:00")),
      ("b1",Timestamp.valueOf("2016-01-01 00:00:00")),
      ("c1",Timestamp.valueOf("2017-01-01 00:00:00"))
    )).toDF("key","value")
    /******************************/

    val transformed = for {
      pipeline <- decode[Pipeline](args(0))
      data = spark.read.parquet(pipeline.datasetUri).cache
      transfom <- ingest(data, pipeline)
    } yield (transfom, outputUri(pipeline.datasetUri))

    transformed.foreach {
      case (data, uri) => data.write.format("parquet").save(uri)
    }

    spark.stop()
  }
}
