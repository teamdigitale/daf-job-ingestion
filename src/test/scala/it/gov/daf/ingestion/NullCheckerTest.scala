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

import it.gov.daf.ingestion.model.NullFormat
import org.scalatest.FunSuite
import com.holdenkarau.spark.testing.DataFrameSuiteBase
import it.gov.daf.ingestion.transformations._
import java.sql.{Date, Timestamp}

class NullCheckerTest extends FunSuite with DataFrameSuiteBase {
  test("nullChecker test") {
    val sqlCtx = sqlContext
    import sqlCtx.implicits._

    val input1 = sc.parallelize(List("a", "b", "c")).toDF("value")

    val output1 = nullChecker(input1, NullFormat("value"))

    assertDataFrameEquals(input1, output1)

    val input2 = sc.parallelize(List("", "b", "c")).toDF("value")

    val expectedOutput2 = sc.parallelize(List(null, "b", "c")).toDF("value")

    val output2 = nullChecker(input2, NullFormat("value"))

    assertDataFrameEquals(output2, expectedOutput2)

    val input3 = sc.parallelize(List(("",""), ("b1","b2"), ("c1","c2"))).toDF("key","value")

    val expectedOutput3 = sc.parallelize(List((null,null), ("b1","b2"), ("c1","c2"))).toDF("key","value")

    val output3 = nullChecker(nullChecker(input3, NullFormat("value")), NullFormat("key"))

    assertDataFrameEquals(output3, expectedOutput3)

    val input4 = sc.parallelize(List(("","a2"), ("b1",""), ("c1","c2"))).toDF("key","value")

    val expectedOutput4 = sc.parallelize(List((null,"a2"), ("b1",null), ("c1","c2"))).toDF("key","value")

    val output4 = nullChecker(nullChecker(input4, NullFormat("value")), NullFormat("key"))

    assertDataFrameEquals(output4, expectedOutput4)

  }
}
