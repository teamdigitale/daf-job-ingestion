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
import org.apache.spark.sql.{ DataFrame, Column }
import org.apache.spark.sql.functions._

import it.gov.daf.ingestion.model._

object CodecTransformer {

  def codecTransformer(implicit config: Config) = GenericTransformer(codecFormatter)

  // TODO If there is no encoding, add a check for actual encoding or a guessing algorithm
  private def codecFormatter(data: DataFrame, colFormat: Format)  = {

    val colName = colFormat.column

    data.withColumn(colName, colFormat.sourceEncoding.fold(col(colName)) (enc => decode(encode(col(colName), enc), "UTF-8")))
  }

}
