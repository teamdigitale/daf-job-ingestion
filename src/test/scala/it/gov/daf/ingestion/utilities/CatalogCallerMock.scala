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
import it.gov.daf.catalogmanager.MetaCatalog

object CatalogCallerMock {

  def catalog(dsUri: String): EitherT[Task, IngestionError, MetaCatalog] = {

    val catalogFromResource = decode[MetaCatalog](Source.fromURL(getClass.getClassLoader.getResource(dsMap(dsUri))).mkString).leftMap(ResponseError(_))
      EitherT.fromEither[Task](catalogFromResource)

    // ???
  }

  private val dsMap = Map (
    "daf://dataset/ord/ale.ercolani/default_org/TRAN/terrestre/carsharing_entity_vehicle" ->
      "carsharing_entity_vehicle.json"
  )

}
