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
import scala.concurrent.Await
import scala.concurrent.duration._
import pureconfig.loadConfig
import pureconfig.error.ConfigReaderFailures
import monix.execution.Scheduler.Implicits.global
import org.scalatest.FunSuite
import java.sql.{Date, Timestamp}
import Ingestion._
import io.circe.generic.auto._, io.circe._
import io.circe.parser.decode
import cats._,cats.data._
import cats.implicits.{ catsSyntaxEq => _, _ }
import com.typesafe.config.Config
import monix.eval.Task

import it.gov.daf.ingestion.transformations._
import it.gov.daf.ingestion.model._
import it.gov.daf.ingestion.client.CatalogClient
import it.gov.daf.ingestion.client.CatalogCaller._
import it.gov.daf.ingestion.client.CatalogCallerMock._
import it.gov.daf.ingestion.utilities._
import it.gov.daf.catalog_manager.yaml.MetaCatalog
import scala.util.{Success, Failure}

class CatalogClientTest extends FunSuite {

  // This test needs a working catalog-manager defined in configuration
  test("catalog test") {

    val dsUri="daf://dataset/ord/ale.ercolani/default_org/TRAN/terrestre/carsharing_entity_vehicle"

    val transformedFromService: EitherT[Task,IngestionError, MetaCatalog] = catalogFromResource(dsUri)

    val transformedFromResource: EitherT[Task,IngestionError, MetaCatalog] = catalogFromResource(dsUri)

    val resultFS = Await.result(transformedFromService.value.runAsync, 10.seconds)
    val result   = Await.result(transformedFromResource.value.runAsync, 10.seconds)

    assert(result == resultFS)

  }

}
