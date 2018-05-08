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
import it.gov.daf.ingestion.transformations._
import it.gov.daf.ingestion.transformations.DateTransformer._
import java.sql.{Date, Timestamp}
import Ingestion._
import cats._,cats.data._
import cats.implicits._
import it.gov.daf.ingestion.model.Format
import com.typesafe.config.Config

class IngestionTest extends FunSuite with DataFrameSuiteBase {

  test("Ingestion test") {
    val sqlCtx = sqlContext
    import sqlCtx.implicits._

    implicit val sess = sqlCtx.sparkSession

    implicit val config: Config = null

    val input1 = sc.parallelize(List(
      ("",  "2015-01-01 00:00:00"),
      ("b1","2016-01-01 00:00:00"),
      ("c1","2017-01-01 00:00:00")
    )).toDF("key","value")

    val transformations: List[Transformation] =
      List(GenericTransformer(nullChecker).transform(List(Format("key", None, None, None)))
        , dateTransformer.transform(List(Format("value", None, None, None)))
      )

    val output1 = transformations.map(Kleisli(_)).reduceLeft(_.andThen(_)).apply(input1)

    output1.foreach(_.collect.foreach(println))

    assertDataFrameEquals(input1, input1)

  }

}
