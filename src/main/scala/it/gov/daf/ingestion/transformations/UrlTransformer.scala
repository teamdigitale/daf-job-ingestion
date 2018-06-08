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

package it.gov.daf.ingestion.transformations

import com.typesafe.config.Config
import org.apache.spark.sql.{ Column, DataFrame, Row, SQLContext }
import org.apache.spark.sql.functions._

import it.gov.daf.ingestion.model._

object UrlTransformer {

  private val colPrefix = "__norm_"

  def urlTransformer// (implicit config: Config)
  = GenericTransformer[UrlFormat](urlFormatter)

  val urlFormatter = { (data: DataFrame, colFormat: UrlFormat) =>
    val colName = colFormat.column
    def normalize(colName: String) :Column = {
      val column = col(colName)
      val urlSplit = split(col(colName), "http[s]*://")
      when(size(urlSplit) > 1, column).otherwise(concat(lit("http://"), column))
    }

    data.withColumn(s"$colPrefix$colName", normalize(colName))
  }

}
