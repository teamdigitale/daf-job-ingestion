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

case class StdColInfo(vocUri: String, vocPath: String, vocProp: String, propHierarchy: List[String], colGroup: String) extends Serializable {
  override def toString =
    s"""StdColInfo("vocUri=$vocUri, vocPath=$vocPath, vocProp=$vocProp propHierarchy=List(${propHierarchy.mkString("|")}), colGroup=$colGroup)"""
}

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
