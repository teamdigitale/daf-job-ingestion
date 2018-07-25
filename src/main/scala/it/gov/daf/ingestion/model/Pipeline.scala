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

package it.gov.daf.ingestion.model

// import io.circe.generic.extras._, io.circe.syntax._, io.circe.generic.auto._, io.circe._

case class VocInfo(vocPath: String, propHierarchy: List[String])

case class StdColInfo(vocUri: String, vocPath: String, vocProp: String, propHierarchy: List[String], colGroup: String) extends Serializable

sealed trait Format extends Product with Serializable {
  def column: String
}

case class StdFormat(column: String, colInfo: StdColInfo) extends Format
case class DateFormat(column: String, sourceDateFormat: String) extends Format
case class UrlFormat(column: String) extends Format
case class AddressFormat(column: String) extends Format
case class CodecFormat(column: String, sourceEncoding: Option[String]) extends Format
case class NullFormat(column: String, nullConventions: List[String]) extends Format

case class IngestionStep(name: String, priority: Int, stepDetails: List[Format])

case class Pipeline(datasetName: String, datasetPath: String, encoding: Option[String], steps: Steps)


// Sample value

// val pipeline = Pipeline(datasetName = "data_std_test_2",
//   datasetPath = "src/main/resources/data_std_test_2/",
//   steps = List(IngestionStep(priority = 1, name = "standardization",
//     stepDetails = List(
//       StdFormat(column = "licenza_code_1",
//         colInfo = StdColInfo(vocUri = "daf://voc/TECH__scienza/voc_licenze", vocPath = "src/main/resources/licences/", vocProp = "code_level_1", propHierarchy = List("code_level_1,label_level_1"), colGroup = "licences")),
//       StdFormat(column = "licenza_code_2",
//         colInfo = StdColInfo(vocUri = "daf://voc/TECH__scienza/voc_licenze", vocPath = "src/main/resources/licences/", vocProp = "code_level_1", propHierarchy = List("code_level_1,label_level_1"), colGroup = "licences")),
//       StdFormat(column = "licenza_code_3",
//         colInfo = StdColInfo(vocUri = "daf://voc/TECH__scienza/voc_licenze", vocPath = "src/main/resources/licences/", vocProp = "code_level_1", propHierarchy = List("code_level_1,label_level_1"), colGroup = "licences"))))))
