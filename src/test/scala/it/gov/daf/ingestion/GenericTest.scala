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

import it.gov.daf.ingestion.model.Format
import org.scalatest.FunSuite
import com.holdenkarau.spark.testing.DataFrameSuiteBase
import it.gov.daf.ingestion.transformations._

class GenericTest extends FunSuite with DataFrameSuiteBase {

  test("simple test") {
    val sqlCtx = sqlContext
    import sqlCtx.implicits._

    val input1 = sc.parallelize(List(1, 2, 3)).toDF
    assertDataFrameEquals(input1, input1) // equal
    val input2 = sc.parallelize(List(4, 5, 6)).toDF
    intercept[org.scalatest.exceptions.TestFailedException] {
      assertDataFrameEquals(input1, input2) // not equal
    }
  }

}
