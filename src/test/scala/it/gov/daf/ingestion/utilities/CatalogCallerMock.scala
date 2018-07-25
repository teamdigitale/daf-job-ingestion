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

package it.gov.daf.ingestion.client

import scala.io.Source
import io.circe.generic.auto._, io.circe._
import io.circe.parser.decode
import cats._,cats.data._
import cats.implicits._
import cats.data.EitherT._
import monix.eval.Task

import it.gov.daf.ingestion.model._
import it.gov.daf.catalog_manager.yaml.MetaCatalog

object CatalogCallerMock {

  private val dsMap = Map (
    "daf://dataset/ord/ale.ercolani/default_org/TRAN/terrestre/carsharing_entity_vehicle" ->
      "carsharing_entity_vehicle.json",
    "daf://voc/ECON__impresa_contabilita/ds_aziende" ->
      "ds_aziende.json",
    "daf://voc/REGI__europa/voc_territorial_classification" ->
      "voc_territorial_classification.json",
    "daf://voc/TECH__scienza/voc_licenze" ->
      "voc_licenze.json"
  )

  def localCatalog(fileName: String) = decode[MetaCatalog](Source.fromURL(getClass.getClassLoader.getResource(fileName)).mkString).leftMap(ResponseError(_))

  def catalogFromResource(dsUri: String): EitherT[Task, IngestionError, MetaCatalog] =
    EitherT.fromEither[Task](localCatalog(dsMap(dsUri)))

}
