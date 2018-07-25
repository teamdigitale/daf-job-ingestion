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

import cats._,cats.data._
import cats.implicits._
import cats.data.EitherT._
import cats.mtl.implicits._
import monix.eval.Task

import it.gov.daf.catalog_manager.yaml.{ Semantic, MetaCatalog, FormatStd, KeyValue, KeyValues }
import model._
import utilities._
import client._
// import client.CatalogCaller._
// import it.gov.daf.ingestion.client.CatalogCallerMock._

object PipelineBuilder {

  private def vocInfo(sem: Semantic)(implicit catalog: CatalogBuilder): Ingestion[VocInfo] = {
    for {
      vocUri     <- sem.uri_voc ||> CatalogError("Url not found in vocabulary metadata")
      vocCatalog <- catalog(vocUri)
      uri        <- vocCatalog.operational.physical_uri ||> CatalogError("Physical uri not found in vocabulary metadata")
      hierarchy  <- (sem.uri_property >>= DataCatalog(vocCatalog).hierarchy) ||> CatalogError("Hierarchy not found in vocabulary metadata")
    } yield {
      VocInfo(uri, hierarchy)
    }
  }

  //TODO field_group is mandatory?
  private def colInfo(sem: Semantic): VocInfo => Option[StdColInfo] =
    vocInfo => for {
      vocUri    <- sem.uri_voc
      vocProp   <- sem.uri_property
      colGroup  <- sem.field_group.orElse(Some(""))
    } yield {
      StdColInfo(vocUri, vocInfo.vocPath, vocProp, vocInfo.propHierarchy, colGroup)
    }

  private def stdInfo(sem: Semantic)(implicit catalog: CatalogBuilder): Ingestion[StdColInfo] = {
    vocInfo(sem) >>= (colInfo(sem)(_) ||> CatalogError("Vocabolary info not found in dataset metadata"))
  }
//FormatStd(name: String, param: Option[List[KeyValue]], conv: Option[List[KeyValueArray]])
  def buildPipeline(catalog: MetaCatalog)(implicit catalogBuilder: CatalogBuilder): Ingestion[Pipeline] = {

    val cata = DataCatalog(catalog)
    import cata._

    val steps: Ingestion[Steps] = transformations.collect {
      case "values to null" => asIngestion(IngestionStep("values to null", 1, typedColumns.collect {
        case (name, Some(FormatStd(_, _, Some(conv)))) => conv.collect {
          case KeyValues("null", value) => NullFormat(name, value.toList)
        }
      }.flatten))
      case "date to ISO8601" => asIngestion(IngestionStep("date to ISO8601", 1, typedColumns.collect {
        case (name, Some(FormatStd(_, Some(params), _))) => params.collect {
          case KeyValue("format", format) => DateFormat(name, format)
        }
      }.flatten))
      case "url normalizer" => asIngestion(IngestionStep("url normalizer", 1, typedColumns.collect {
        case (name, Some(FormatStd(_, Some(params), _))) => params.collect {
          case KeyValue("uri", _) => UrlFormat(name)
        }
      }.flatten))
      case "address normalizer" => asIngestion(IngestionStep("address normalizer", 1, typedColumns.collect {
        case (name, Some(FormatStd("address", _, _))) => AddressFormat(name)
        }))
      case "standardization" =>
        val formats = semanticColumns.collect {
          case (name, sema@Some(Semantic(_, _, _, _, _, _, Some(uri_voc), _, _, Some(uri_property), _))) => stdInfo(sema.get).map(StdFormat(name, _))}.sequence
        formats.map(IngestionStep("standardization", 1, _))
      case err =>  fromEither[Task](Left(CatalogError(s"Wrong pipeline step [$err] in catalog")): Either[IngestionError, IngestionStep])
    }.sequence

    // physical_uri is optional hor historical reason
    steps.map( s => Pipeline(op.logical_uri, op.physical_uri.getOrElse(""), dataschema.encoding, s))
  }

}
