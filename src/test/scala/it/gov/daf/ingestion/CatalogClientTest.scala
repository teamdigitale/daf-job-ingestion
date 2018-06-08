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
// import it.gov.daf.ingestion.client.CatalogCaller._
import it.gov.daf.ingestion.client.CatalogCallerMock._
import it.gov.daf.ingestion.utilities._
import it.gov.daf.catalogmanager.MetaCatalog
import scala.util.{Success, Failure}

class CatalogClientTest extends FunSuite {

  test("catalog test") {

    val dsUri="daf://dataset/ord/ale.ercolani/default_org/TRAN/terrestre/carsharing_entity_vehicle"

    val catalogFromResource: Either[IngestionError, MetaCatalog] =
      decode[MetaCatalog](Source.fromURL(getClass.getClassLoader.getResource("carsharing_entity_vehicle.json")).mkString).leftMap(ResponseError(_))

    val transformedFromService: EitherT[Task,IngestionError, MetaCatalog] = catalog(dsUri)

    val transformed: EitherT[Task,IngestionError, MetaCatalog] =
      EitherT.fromEither[Task](catalogFromResource)

    import scala.concurrent.Await
    import scala.concurrent.duration._

    val task = transformed.value
    // val completed = for (r <- task) { println(r) }

    // Await.result(completed, 3.seconds)

    // Works for value read from config
    // task.runAsync.foreach { result => println(result) }


    // val compl = transformedFromService.value.runAsync
    // val compl = transformedFromService.value.runAsync

    val resultFS = Await.result(transformedFromService.value.runAsync, 10.seconds)
    val result   = Await.result(transformed.value.runAsync, 10.seconds)


    // println(result)
    // transformedFromService.value.runAsync.foreach { result => println(result) }


    // val pp: Nothing = transformedFromService.value.runAsync

    // transformedFromService.value.runOnComplete { result =>
    //   println(result)

    //   result match {
    //     case Success(value) =>
    //       println(value)
    //     case Failure(ex) =>
    //       System.err.println(s"ERROR: ${ex.getMessage}")
    //   }
    // }

    //   runAsync.foreach {_.fold (
    //   l => println(s"left: $l"),
    //   r => println(s"right: $r")
    // )


  // }
    // assert("true" === "true")
    assert(result == resultFS)

  }

}
