/*
 * Copyright 2017-2018 TEAM PER LA TRASFORMAZIONE DIGITALE
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

import cats._, cats.data._
import cats.implicits._

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.DataFrame

import catalog_manager.yaml.DatasetCatalog
import it.gov.daf.ingestion.model._
import org.apache.spark.sql.functions._

package transformations {

  trait Transformer extends Serializable {
    // TBD We have two signatures until we define the parameters
    def transform(catalog: DatasetCatalog)(implicit spark: SparkSession): Transformation
    def transform(columns: List[String])(implicit spark: SparkSession): Transformation
    def name: String
  }

}

package object transformations {

  type TransformationResult = Either[IngestionError, DataFrame]

  type Transformation = DataFrame => TransformationResult

  type DataTransformation = (DataFrame, String) => DataFrame

  val nullChecker = { (data: DataFrame, colName: String) =>
    data.withColumn(colName, when(col(colName) === "", null).otherwise(col(colName)))
  }

  val dateFormatter = { (data: DataFrame, colName: String) =>
    data.withColumn(colName, date_format(col(colName), "YYYY-MM-DD hh:mm:ss"))
  }

  val urlFormatter = { (data: DataFrame, colName: String) =>
    data.withColumn(colName, split(col(colName), "http[s]://"))
  }

}
