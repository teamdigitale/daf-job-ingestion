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

import java.sql.{Date, Timestamp}
import org.apache.spark.sql.Row
import org.scalatest.FunSuite
import com.holdenkarau.spark.testing.DataFrameSuiteBase
import it.gov.daf.ingestion.model.DateFormat
import it.gov.daf.ingestion.transformations._
import it.gov.daf.ingestion.transformations.DateTransformer._
import utilities.MockData._

class DateFormatterTest extends FunSuite with DataFrameSuiteBase {

  test("dateFormatter test with filled format") {
    val sqlCtx = sqlContext
    import sqlCtx.implicits._

    val input1 = sc.parallelize(List("2015-03-14 09:00:00")).toDF("value")

    val outputComputed = dateFormatter(input1, DateFormat("value", "yyyy-MM-dd hh:mm:ss"))

    val expectedData = sc.parallelize(List(("2015-03-14 09:00:00","2015-03-14 09:00:00",2015,3,14,11,73))).map(Row.fromTuple)
    val expectedOutput = sqlCtx.createDataFrame(expectedData, dateSchema)

    assertDataFrameEquals(outputComputed, expectedOutput)
  }

 test("dateFormatter test with inferred format") {
    val sqlCtx = sqlContext
    import sqlCtx.implicits._

    val input1 = sc.parallelize(List("2015-03-14 09:00:00")).toDF("value")

    val outputComputed = dateFormatter(input1, DateFormat("value", ""))

    val expectedData = sc.parallelize(List(("2015-03-14 09:00:00","2015-03-14 09:00:00",2015,3,14,11,73))).map(Row.fromTuple)
    val expectedOutput = sqlCtx.createDataFrame(expectedData, dateSchema)

    assertDataFrameEquals(outputComputed, expectedOutput)
  }

}
