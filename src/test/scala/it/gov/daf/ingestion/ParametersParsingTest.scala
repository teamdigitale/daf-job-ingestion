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

import scala.io.Source
import io.circe.generic.auto._, io.circe._
import io.circe.parser.decode
import it.gov.daf.ingestion.model._

import org.scalatest.FunSuite

class PrametersParsingTest extends FunSuite  {
  test("Parsing parameters test") {
    val parameters = Source.fromURL(getClass.getClassLoader.getResource("movimenti-turistici-params.json"))

    val output = decode[Pipeline](parameters.mkString)

    val expected = Right(Pipeline("test","/daf/ordinary/default_org/EDUC__turismo/default_org_o_reg_sardegna_mov_turistici",
      List(IngestionStep("text to utf-8", 1,
        List(Format("provincia",None,None,Some("UTF-8")),
          Format("macrotipologia",None,None,Some("UTF-8")),
          Format("descrizione",None,None,Some("UTF-8")))),
        IngestionStep("empty values to null",2,
          List(Format("provincia",None,None,None),
            Format("macrotipologia",None,None,None),
            Format("descrizione",None,None,None))))))

    assert(output === expected)
  }
}
