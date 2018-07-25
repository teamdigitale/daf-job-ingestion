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

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.scalatest.FunSuite
import com.holdenkarau.spark.testing.DataFrameSuiteBase
import it.gov.daf.ingestion.model.AddressFormat
import it.gov.daf.ingestion.transformations._
import it.gov.daf.ingestion.transformations.AddressTransformer._

import utilities.MockData._

class AddressTransformerTest extends FunSuite with DataFrameSuiteBase {

  test("addressTransformer test") {
    val sqlCtx = sqlContext
    import sqlCtx.implicits._
    implicit val config: Config = ConfigFactory.load

    val colName = "address"

    val input = sc.parallelize(List("piazza della vergine Immacolata 00144 Roma (ROMA) Lazio Italia",
      "via piave, 25 Rocca di papa (RM)",
      "Corso V. Emanuele, 2 Sant'angelo dei lombardi Roccapriora 00100 Italia")).toDF(colName)

    val output = addressFormatter(input, AddressFormat(colName))

    val expectedOutput = sc.parallelize(expectedAddress)
      .toDF("address", "__address_parseResult_address", "__address_placename_address", "__address_cityname_address",
        "__address_postcode_address", "__address_provname_address", "__address_countryname_address" )

    assertDataFrameEquals(output, expectedOutput)

  }
}
