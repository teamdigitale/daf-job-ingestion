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

import io.circe.generic.extras._, io.circe.syntax._, io.circe.generic.auto._, io.circe._

case class StdColInfo(vocUri: String, vocPath: String, vocProp: String, propHierarchy: List[String], colGroup: String) extends Serializable

case class StdInfo(colnName: String, stdColInfo: StdColInfo)

case class Format(column: String, standardization: Option[StdColInfo], sourceDateFormat: Option[String]
  , sourceEncoding: Option[String])

case class IngestionStep(name: String, priority: Int, stepDetails: List[Format])

//change datasetUri into datasetPath
case class Pipeline(datasetName: String, datasetUri: String, steps: Steps)
