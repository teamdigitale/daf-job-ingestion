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

import java.text.SimpleDateFormat
import cats._, cats.data._
import cats.implicits._
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.{ DataFrame, Row, SQLContext }
import org.apache.spark.sql.functions._
import com.softwaremill.sttp._
import com.softwaremill.sttp.circe._
import com.softwaremill.sttp.okhttp.OkHttpSyncBackend
import io.circe._, io.circe.generic.auto._
import io.circe.parser._
import io.circe.generic.JsonCodec, io.circe.syntax._
import it.gov.daf.ingestion.model._


case class AddressField(label: String, value: String)

case class PostalQuery(query: String)

object AddressTransformer {

  implicit val backend = OkHttpSyncBackend()

  // type PostalAddress = List[AddressField]

  case class PostalAddress(fields: List[AddressField]) {
    def field(label: String): String = fields.find(_.label == label).fold("")(_.value)
  }

  private val colAdded = "__address_"
  val uri = uri"http://localhost:8080/parser"

  def addressTransformer = GenericTransformer(addressFormatter)

  private def convertError[A](err: Either[String, Either[Error,A]]): Either[ServiceError, A] = {
    err match {
      case Left(error)        => Left(EndpointError(error))
      case Right(Left(error)) => Left(ResponseError(error))
      case Right(Right(a))    => Right(a)
    }
  }

  def addressFields(body: PostalQuery): Either[ServiceError, List[AddressField]] = {
    convertError { sttp
      .post(uri)
      .body(body)
      .response(asJson[List[AddressField]]).send().body }
  }

  def resolveAddress: String => String = { field =>
    addressFields(PostalQuery(field)).map(_.asJson.toString).getOrElse("")
  }

  val addressUdf = udf(resolveAddress, StringType)

  def extractField(address: String, label: String): String = {
    if (address.isEmpty)
      ""
    else {
      val value = for {
        fields <- io.circe.parser.decode[List[AddressField]](address).toOption
        value <- fields.find(_.label == label)
      } yield value
      value.fold("")(_.value)
    }
    // ???
  }

  val addressFieldUdf = udf(extractField _, StringType)

  def addressFormatter(data: DataFrame, colFormat: Format)  = {
    val colName = colFormat.name
    // val address = addressFields(PostalQuery(colFormat
    val addressAdded = data.withColumn(s"${colAdded}$colName", addressUdf(col(colName)))

    addressAdded.withColumn(s"${colAdded}placename_$colName", addressFieldUdf(col(s"${colAdded}$colName"), lit("road")))
      .withColumn(s"${colAdded}cityname_$colName", addressFieldUdf(col(s"${colAdded}$colName"), lit("city")))
      .withColumn(s"${colAdded}postcode_$colName", addressFieldUdf(col(s"${colAdded}$colName"), lit("postcode")))
      .withColumn(s"${colAdded}provname_$colName", addressFieldUdf(col(s"${colAdded}$colName"), lit("state_district")))
      .withColumn(s"${colAdded}countryname_$colName", addressFieldUdf(col(s"${colAdded}$colName"), lit("country")))
  }

}
