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

import java.net.URLEncoder
// import play.api.libs.ws.WSClient
// import play.api.libs.ws.ahc.AhcWSClient
import com.softwaremill.sttp.asynchttpclient.monix._
import com.softwaremill.sttp._
import com.softwaremill.sttp.circe._
import io.circe.generic.extras._, io.circe.syntax._, io.circe.generic.auto._
import io.circe.Error
import it.gov.daf.catalog_manager.yaml.MetaCatalog
import scala.concurrent.{ Future, ExecutionContext }
import it.gov.daf.ingestion.services._
import cats._,cats.data._
import monix.eval.Task

import it.gov.daf.ingestion.model._

object CatalogClient {

  type CatalogService = Service[MetaCatalog]

  // import akka.actor.ActorSystem
    // import akka.stream.ActorMaterializer
    // implicit val system: ActorSystem = ActorSystem()
    // implicit val materializer: ActorMaterializer = ActorMaterializer()

    // implicit val ws: WSClient = AhcWSClient()

  def apply(auth: User, uriCatalogManager: String) = {
    lazy val catalogManagerClient = new CatalogClient(auth, uriCatalogManager)
    catalogManagerClient
  }


}

class CatalogClient private (user: User, uriCatalogManager: String) {
  import CatalogClient._

  implicit val sttpAuth: PartialRequest[String, Nothing] = sttp.auth.basic(user.login, user.password)

  def getCatalog(logicalUri: String): CatalogService = {
    //Anytime the param is a uri, you need to run this
    val logicalUriEncoded = URLEncoder.encode(logicalUri, "UTF-8")

    val dsUri = s"$uriCatalogManager/$logicalUriEncoded"

    // TODO The following auth (test:test) is for calling a test catalog manager
    // val testAuth ="Basic YWxlOmFsZQ=="

    // catalogManagerClient.datasetcatalogbyid(auth, logicalUriEncoded)
    serviceGet(uri"$dsUri")

  }

  // I know: this name is ugly...
  // def askCatalog(logicalUri: String)(implicit ec: ExecutionContext): Service[MetaCatalog] =
  //   future2Task(getCatalog(logicalUri))


}
