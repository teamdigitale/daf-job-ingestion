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

import cats.data._
import cats.implicits._
import com.typesafe.config.ConfigFactory
import io.circe.generic.auto._
import io.circe.parser.decode
import it.gov.daf.ingestion.model._
import it.gov.daf.ingestion.transformations._
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import pureconfig.loadConfig
import com.typesafe.config.ConfigFactory

import scala.io.Source

object StandardizationTest {

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
    println(s"pipeline: $pipeline")
    // val allTransformations = getTransformations(ConfigFactory.load)

    val transformations: List[Transformation] =
      rawSaver +: commonTransformation +: pipeline.steps.sortBy(_.priority).map(s => allTransformations(s.name).transform(s.stepDetails))

    transformations.map(Kleisli(_)).reduceLeft(_.andThen(_)).apply(data)
  }


  def main(args: Array[String]): Unit = {

    // TBD This will be the result of conversion from logical to physical URI
    //val dsUri = ""

    //val postalUri: Broadcast[String]  = spark.sparkContext.broadcast(args(1))

    // val data = spark.read.parquet(dsUri).cache

    /* TODO: Remove this
     *  This is only for testing the Kylo integration
     */

    //Create data first
    createData()
    //createDataVoc()

    //This is just for testing purposes. Need to be passed from outside
    val parameters = Source.fromURL(getClass.getClassLoader.getResource("StdLicenzeTest.json"))



    val transformed = for {
      //pipeline <- decode[Pipeline](args(0))
      //data = spark.read.parquet(pipeline.datasetUri).cache
      pipeline <- decode[Pipeline](parameters.mkString)
      data = spark.read.parquet(pipeline.datasetPath).cache
      transfom <- ingest(data, pipeline)
    } yield (transfom, outputUri(pipeline.datasetPath))

    // println(transformed.right.get._1.show())
    println(transformed)


    transformed.foreach {
      //case (data, uri) => data.write.format("parquet").save(uri)
      case (data, uri) => data.write.mode(SaveMode.Overwrite).save("src/main/resources/output")
    }


    spark.stop()
    //println(decode[Pipeline](parameters.mkString))


    //val parameters = Source.fromURL(getClass.getClassLoader.getResource("movimenti-turistici-params.json"))

    //val output = decode[Pipeline](parameters.mkString)
    // println(output)

  }

  def createData() ={
    import java.sql.Timestamp
    val sc = spark.sparkContext
    import spark.sqlContext.implicits._

    val data = spark.read.format("csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .load("src/main/resources/data_std_test_2.csv")

    data.printSchema()

    data.write.mode(SaveMode.Overwrite).save("src/main/resources/data_std_test_2")

  }

  def createDataVoc() ={
    import java.sql.Timestamp
    val sc = spark.sparkContext
    import spark.sqlContext.implicits._

    val voc_licenze = spark.read.format("csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .load("src/main/resources/licences.csv")

    voc_licenze.printSchema()

    voc_licenze.write.mode(SaveMode.Overwrite).save("src/main/resources/licences")

  }

}
