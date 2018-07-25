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

package it.gov.daf.ingestion.model

import it.gov.daf.catalog_manager.yaml.{ MetaCatalog, FlatSchema, FormatStd, Semantic }

import cats._
import cats.data._
import cats.implicits._
import cats.mtl.implicits._


case class DataCatalog(catalog: MetaCatalog) extends Serializable {

  val op = catalog.operational
  val dataschema = catalog.dataschema

  // TODO Check metacalog schema for ingestion_pipeline
  val transformations: List[String] =  op.ingestion_pipeline.getOrElse(Nil).toList
  val schema: List[FlatSchema] = dataschema.flatSchema.toList
  val typedColumns: List[(String, Option[FormatStd])] = schema.map { s =>
    val fieldType = s.metadata >>= (_.format_std)
    (s.name, fieldType)
  }

  val semanticColumns: List[(String, Option[Semantic])] = schema.map { s =>
    val semantics = s.metadata >>= (_.semantics)
    (s.name, semantics)
  }

  val colName: Seq[String] = dataschema.flatSchema.map(field => field.name)

  val ontoTag = dataschema.flatSchema.map { field =>
    field.name -> field.metadata.flatMap(_.semantics.map(_.id))
  }.toMap.collect {
    case (k, Some(a)) if a =!= "" => (k, a)
  }

  def hierarchy(colName: String): Option[List[String]] =
    schema.collect {
      case FlatSchema(name, _, Some(metadata)) if name == colName =>
        for {
          sem       <- metadata.semantics
          hierarchy <- sem.property_hierarchy
        } yield {hierarchy}
    }.headOption.flatten.map(_.toList)



  // val rawHierc: List[(String, (String, List[String], String, String))] = ontoTag.mapValues { x =>
  //   val info = ontoMgr.getOntoInfoField(x)
  //   (x, info.hierc, info.vocName, info.vocCol)
  //   }.toList

// val colInfo: Option[String => StdFormat]  = for {
//       meta      <- s.metadata
//       sem       <- meta.semantics
//       vocProp   <- sem.uri_property
//       hierarchy <- sem.property_hierarchy
//       colGroup  <- sem.field_group
//     } yield {
//       vocPath => StdFormat(s.name, StdColInfo(vocUri, vocPath, vocProp, hierarchy, colGroup))
//     }

}
