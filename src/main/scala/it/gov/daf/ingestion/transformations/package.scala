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

import java.sql.Timestamp
import java.time.LocalDateTime
import com.typesafe.config.Config
import org.apache.spark.sql.{ SparkSession, DataFrame }
import org.apache.spark.sql.functions._

import cats._, cats.data._
import cats.implicits._

import it.gov.daf.ingestion.model._
import it.gov.daf.ingestion.transformations.DateTransformer.dateTransformer
import it.gov.daf.ingestion.transformations.NullChecker.nullTransformer
import it.gov.daf.ingestion.transformations.CodecTransformer.codecTransformer
import it.gov.daf.ingestion.transformations.UrlTransformer.urlTransformer

package transformations {

  trait Transformer extends Serializable {
    def transform(formats: List[Format])(implicit spark: SparkSession): Transformation
  }

}

package object transformations {

  type TransformationResult = Either[IngestionError, DataFrame]

  type Transformation = DataFrame => TransformationResult

  type DataTransformation = (DataFrame, Format) => DataFrame

  type Transformations = Map[String, Transformer]

  val rawSaver: Transformation  =  { data =>
    Right(data.columns.foldLeft(data)( (data, colName) =>
      data.withColumn(s"__raw_$colName", col(colName))))
  }

  val commonTransformation: Transformation  =  { data =>
    Right(data.withColumn("__ROWID", hash(data.columns.map(col):_*))
      .withColumn("__dtcreated", lit(unix_timestamp))
      .withColumn("__dtupdate", col("__dtcreated")))
  }

  val nullChecker = { (data: DataFrame, colFormat: Format) =>
    val colName = colFormat.column
    data.withColumn(colName, when(col(colName) === "", null).otherwise(col(colName)))
  }

  def getTransformations(implicit config: Config) = Map(
    "text to utf-8" -> codecTransformer,
    "empty values to null" -> nullTransformer,
    "date to ISO8601" -> dateTransformer,
    "url normalizer" -> urlTransformer
  )
}
