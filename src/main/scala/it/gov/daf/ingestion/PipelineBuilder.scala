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

import it.gov.daf.catalogmanager._
import it.gov.daf.ingestion.model._
import cats._,cats.data._
import cats.implicits._
// import it.gov.daf.common.utils._

object PipelineBuilder {

  def buildPipeline(catalog: MetaCatalog): Either[IngestionError, Pipeline] = {
    val op = catalog.operational
    // TODO Check metacalog schema for ingestion_pipeline
    val transformations: List[String] =  op.ingestion_pipeline.getOrElse(Nil)
    val schema: List[FlatSchema] = catalog.dataschema.flatSchema
    val typedColumns: List[(String, Option[FormatStd])] = schema.map { s =>
      val fieldType = s.metadata >>= (_.format_std)
      (s.name, fieldType)
    }
    val semanticColumns: List[(String, Option[Semantic])] = schema.map { s =>
      val semantics = s.metadata >>= (_.semantics)
      (s.name, semantics)
    }

    val p: List[Option[String => StdFormat]] = schema.map { s =>
      val colInfo: Option[String => StdFormat]  = for {
        meta      <- s.metadata
        sem       <- meta.semantics
        vocUri    <- sem.uri_voc
        vocProp   <- sem.uri_property
        hierarchy <- sem.property_hierarchy
        colGroup  <- sem.field_group
      } yield {
        vocPath => StdFormat(s.name, StdColInfo(vocUri, vocPath, vocProp, hierarchy, colGroup))
      }
      colInfo
    }

    def colInfo(sem: Semantic): Option[StdColInfo] =  {
      val colInfo: Option[StdColInfo]  = for {
        vocUri    <- sem.uri_voc
        vocProp   <- sem.uri_property
        hierarchy <- sem.property_hierarchy
        colGroup  <- sem.field_group
      } yield {
        StdColInfo(vocUri, physicalUri(vocUri
        ), vocProp, hierarchy, colGroup)
      }
      colInfo
    }

    val steps: Steps = transformations.collect {
      case "date to ISO8601" => IngestionStep("date to ISO8601", 1, typedColumns.collect {
        case (name, Some(FormatStd("date", format))) => DateFormat(name, format)
      })
      case "url normalizer" => IngestionStep("url normalizer", 1, typedColumns.collect {
        case (name, Some(FormatStd("url", _))) => UrlFormat(name)
      })
      // case "standardization" => IngestionStep("standardization", 1, semanticColumns.collect {
      //   case (name, Some(semantics)) => StdFormat(name, colInfo(semantics))
      // })

    }
    // TODO Check metacalog schema for physical_uri
    Right(Pipeline(op.logical_uri, op.physical_uri.getOrElse(""), steps))
    // ???
  }

  private def physicalUri(dsUri: String): String = dsUri
}
