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

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.DataFrame


object RawSaver {

  def rawSaver1: Transformation = {
      // def transform(formats: List[Format])
      // (implicit spark: SparkSession): Transformation = { data =>

      //   data.withColumn(colName, colFormat.encoding.fold(col(colName)) (enc => encode(decode(col(colName), enc), "UTF-8")))

      //   val res: DataFrame = formats.foldLeft(data)(normalizer.apply)


  ???
  }


}