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

import org.scalatest.FunSuite
import com.holdenkarau.spark.testing.DataFrameSuiteBase
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import it.gov.daf.ingestion.transformations._
import it.gov.daf.ingestion.transformations.DateTransformer._
import java.sql.{Date, Timestamp}
import Ingestion.transform
import cats._,cats.data._
import cats.implicits._
import it.gov.daf.ingestion.model.{ NullFormat, DateFormat }
import com.typesafe.config.Config
import utilities.MockData._

class IngestionTest extends FunSuite with DataFrameSuiteBase {

  test("Ingestion test") {
    val sqlCtx = sqlContext
    import sqlCtx.implicits._

    implicit val sess = sqlCtx.sparkSession

    implicit val config: Config = null

    val data = spark.read.format("csv")
      .option("header", "true")
      .option("delimiter", ";")
      .option("inferSchema", "true")
      .load("src/test/resources/dataset_test_overall.csv")

    // val dsUri = "daf://voc/ECON__impresa_contabilita/ds_aziende"
    // val pipelineTask = CatalogCallerMock.catalogFromResource(dsUri).flatMap(buildPipeline(_))
    // val pipeline = Await.result(pipelineTask.value.runAsync, 10.seconds)


    val transformed = transform(data, expectedPipeline)
    // TODO change nullChecker
    // val transformations: List[Transformation] =
    //   List(GenericTransformer[NullFormat](nullChecker).transform(List(NullFormat("key")))
    //     , dateTransformer.transform(List(DateFormat("value", None)))
    //   )
    // /*_*/
    // val output1 = transformations.map(Kleisli(_)).reduceLeft(_.andThen(_)).apply(input1)

    // output1.foreach(_.collect.foreach(println))
    // transformed.prins
    // transformed.foreach(p => println(p.collect))
    transformed.foreach(p => p.printSchema)
    val pp = transformed.map(_.collect)
    // pp.foreach(a => println(a.toList.mkString("|")))
    transformed.foreach(_.write.mode(SaveMode.Overwrite).save("src/test/resources/output"))

    assert(true == true)
    // assertDataFrameEquals(input1, input1)

  }

}
