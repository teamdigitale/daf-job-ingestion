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
import java.text.SimpleDateFormat
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.{ DataFrame, Row, SQLContext }
import org.apache.spark.sql.functions._
import java.sql.{Date, Timestamp}
import collection.JavaConverters._

import cats._, cats.data._
import cats.implicits._
import com.joestelmach.natty._

import it.gov.daf.ingestion.model._

object NullChecker {

  private val colAdded = "__norm_"

  def nullTransformer// (implicit config: Config)
  = GenericTransformer[NullFormat](nullFormatter)

  private def nullFormatter(data: DataFrame, colFormat: Format)  = {
    val colName = colFormat.column

    data.withColumn(colName, when(col(colName) === "", null).otherwise(col(colName)))
    // data.withColumn(s"${colAdded}$colName", date_format(timeUdf(col(colName)), "YYYY-MM-DD hh:mm:ss"))
  }

}
