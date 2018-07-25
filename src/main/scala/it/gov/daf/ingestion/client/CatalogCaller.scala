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

import pureconfig.loadConfig
import pureconfig.error.ConfigReaderFailures
import cats._,cats.data._
import cats.implicits._
import cats.data.EitherT._
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import it.gov.daf.ingestion.utilities._
import it.gov.daf.ingestion.model._
import it.gov.daf.catalog_manager.yaml.MetaCatalog
import it.gov.daf.ingestion._

object CatalogCaller {

    lazy val servicesConfig: Either[IngestionError, ServicesUrls] = loadConfig[ServicesUrls](ServicesConfigKey).leftMap(ConfigError)
    lazy val userConfig: Either[IngestionError, User] = loadConfig[User](UserConfigKey).leftMap(ConfigError)

    lazy val catalogClient: Either[IngestionError, CatalogClient] = for {
        config <- servicesConfig
        user   <- userConfig
      } yield {
        CatalogClient(user, config.catalogManagerUrl)
      }

  def catalog(dsUri: String): Ingestion[MetaCatalog] = for {
      client <- fromEither[Task](catalogClient)
      catalog <- client.getCatalog(dsUri).leftWiden
    } yield catalog

}
