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
import it.gov.daf.ingestion.transformations.AddressTransformer._

class AddressTransformerTest extends FunSuite with DataFrameSuiteBase {
  test("addressTransformer test") {
    val sqlCtx = sqlContext
    import sqlCtx.implicits._

    val colName = "url"
    val colPrefix = "__norm_"

    val input1 = sc.parallelize(List("piazza della vergine Immacolata 00144 Roma (ROMA) Lazio Italia",
      "via piave, 25 Rocca di papa (RM)",
      "Corso V. Emanuele, 2 Sant'angelo dei lombardi Roccapriora 00100 Italia")).toDF(colName)

    val output1 = addressFormatter(input1, Format(colName, None, None, None))

    output1.printSchema()
    output1.collect.foreach(println)

    val expectedOutput1 = sc.parallelize(List(("www.google.com", "http://www.google.com"),
      ("http://www.google.com", "http://www.google.com"),
      ("https://www.google.com", "https://www.google.com"))).toDF(colName, s"$colPrefix$colName")

    // assertDataFrameEquals(output1, expectedOutput1)
    assertDataFrameEquals(output1, output1)

  }
}
