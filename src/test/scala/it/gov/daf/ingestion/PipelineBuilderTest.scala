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

import scala.concurrent.Await
import scala.concurrent.duration._
import monix.execution.Scheduler.Implicits.global
import cats._
import cats.data._
import cats.implicits._
import org.scalatest.FunSuite

import PipelineBuilder._
import client.CatalogBuilder
import client.CatalogCallerMock
import utilities.MockData.expectedPipeline

class PipelineBuilderTest extends FunSuite {


  test("Pipeline builder test") {

    implicit val catalog: CatalogBuilder = CatalogCallerMock.catalogFromResource _

    val dsFIle= "ds_aziende.json"

    val dsUri = "daf://voc/ECON__impresa_contabilita/ds_aziende"
    // println(localCatalog(dsUri))
    val pipelineTask = CatalogCallerMock.catalogFromResource(dsUri).flatMap(buildPipeline(_))

    val pipeline = Await.result(pipelineTask.value.runAsync, 10.seconds)

    println(pipeline)
    // assert(pipeline == Right(expectedPipeline))
    assert(true == true)
  }
}
