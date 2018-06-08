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
import org.apache.spark.sql.types.TimestampType
import org.apache.spark.sql.{ DataFrame, Row, SQLContext }
import org.apache.spark.sql.functions._
import java.sql.{Date, Timestamp}
import java.util.{ Date => JDate }
import collection.JavaConverters._

import cats._, cats.data._
import cats.implicits._
import com.joestelmach.natty._

import it.gov.daf.ingestion.model._

object DateTransformer {

  private val colAdded = "__date_"

  def dateTransformer// (implicit config: Config)
  = GenericTransformer[DateFormat](dateFormatter)

  def dateFormatter(data: DataFrame, colFormat: DateFormat)  = {
    val colName = colFormat.column

    def inferDate(date: String): Option[JDate] = {
      val parser = new Parser

      val dates = for {
        groups <- parser.parse(date).asScala.toList
        dates  <- groups.getDates.asScala.toList
      } yield dates

      dates.headOption
    }

    def transformDate: String => Timestamp = { dateField =>

      val date: Option[JDate] = colFormat.sourceDateFormat.map{ format =>
        new SimpleDateFormat(format).parse(dateField)
      }.orElse(inferDate(dateField))

      // date.map(d => new Timestamp(d.getTime).toString).getOrElse("")
      date.map(d => new Timestamp(d.getTime)).getOrElse(new Timestamp(0))
    }


    val timeUdf = udf(transformDate, TimestampType)

    val dateAdded = data.withColumn(s"${colAdded}$colName", date_format(timeUdf(col(colName)), "YYYY-MM-DD hh:mm:ss"))

    dateAdded.withColumn(s"${colAdded}year_$colName", year(col(s"${colAdded}$colName")))
      .withColumn(s"${colAdded}month_$colName", month(col(s"${colAdded}$colName")))
      .withColumn(s"${colAdded}day_$colName", dayofmonth(col(s"${colAdded}$colName")))
      .withColumn(s"${colAdded}week_$colName", weekofyear(col(s"${colAdded}$colName")))
      .withColumn(s"${colAdded}dayofyear_$colName", dayofyear(col(s"${colAdded}$colName")))
      // .withColumn(s"${colAdded}dayofweek_$colName", dayofweek(col(s"${colAdded}$colName")))
  }

}
