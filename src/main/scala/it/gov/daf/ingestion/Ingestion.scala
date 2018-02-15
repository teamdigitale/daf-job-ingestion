/*
 * Copyright 2017-2018 TEAM PER LA TRASFORMAZIONE DIGITALE
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

import cats._,cats.data._
import cats.implicits._

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.{ DataFrame, Row, SQLContext }

import catalog_manager.yaml._
import it.gov.daf.ingestion.transformations._
import it.gov.daf.ingestion.model._

object Ingestion {

  implicit val spark = SparkSession
    .builder
    .appName("Ingestion")
    .getOrCreate()

  // This is the list of all possible transformations
    val allTransformations: List[Transformer] =
      List(GenericTransformer(nullChecker,  "norm_null")
        , GenericTransformer(dateFormatter, "norm_date")
        , GenericTransformer(urlFormatter,  "norm_url")
        , Standardization
      )

    def ingest(data: DataFrame, catalog: DatasetCatalog): Either[IngestionError, DataFrame] = {

      // TBD this list will come from Catalog
      val pipeline: List[String] = List("norm_null", "norm_date", "std_voc")

      val transformations:List[Transformation] = allTransformations.filter(t => pipeline.contains(t.name)).map(_.transform(catalog))

      transformations.map(Kleisli(_)).reduceLeft(_.andThen(_)).apply(data)
    }


  def main(args: Array[String]) = {

    // TBD This will be the result of the parsing of the cmd line parameters
    val dataCatalog: DatasetCatalog = DatasetCatalog(null, null, null)

    // TBD This will be the result of conversion from logical to physical URI
    val dsUri = ""

    // val data = spark.read.parquet(dsUri).cache

    /* TODO: Remove this
     *  This is only for testing the Kylo integration
     */
    import java.sql.{Date, Timestamp}
    val sc = spark.sparkContext
    import spark.sqlContext.implicits._

    val data = sc.parallelize(List(
      ("",  Timestamp.valueOf("2015-01-01 00:00:00")),
      ("b1",Timestamp.valueOf("2016-01-01 00:00:00")),
      ("c1",Timestamp.valueOf("2017-01-01 00:00:00"))
    )).toDF("key","value")
    /******************************/

    // TBD use actual DatasetCatalog
    val transformed = ingest(data, dataCatalog)

    transformed.foreach(_.write.format("parquet").save("/tmp/ingestionTest"))

    spark.stop()
  }
}
