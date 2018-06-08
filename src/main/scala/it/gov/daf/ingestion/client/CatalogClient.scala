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
import play.api.libs.ws.WSClient
import play.api.libs.ws.ahc.AhcWSClient
import it.gov.daf.catalogmanager.MetaCatalog
import it.gov.daf.catalogmanager.client.Catalog_managerClient
import scala.concurrent.{ Future, ExecutionContext }
import it.gov.daf.ingestion.services._
import cats._,cats.data._
import monix.eval.Task

object CatalogClient {

    import akka.actor.ActorSystem
    import akka.stream.ActorMaterializer
    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    implicit val ws: WSClient = AhcWSClient()

  def apply(auth: String, uriCatalogManager: String) = {
    lazy val catalogManagerClient = new Catalog_managerClient(ws)(uriCatalogManager)
    new CatalogClient(auth, catalogManagerClient)
  }


}

class CatalogClient private (auth: String, catalogManagerClient: Catalog_managerClient) {
  import CatalogClient._

  def getCatalog(logicalUri: String): Future[MetaCatalog] = {
    //Anytime the param is a uri, you need to run this
    val logicalUriEncoded = URLEncoder.encode(logicalUri, "UTF-8")

    // TODO The following auth (test:test) is for calling a test catalog manager
    // val testAuth ="Basic YWxlOmFsZQ=="

    catalogManagerClient.datasetcatalogbyid(auth, logicalUriEncoded)
  }

  // I know: this name is ugly...
  def askCatalog(logicalUri: String)(implicit ec: ExecutionContext): Service[MetaCatalog] =
    future2Task(getCatalog(logicalUri))


}
