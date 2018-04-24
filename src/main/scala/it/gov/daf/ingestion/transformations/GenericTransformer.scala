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

import cats._, cats.data._
import cats.implicits._

import com.typesafe.config.Config
import scala.language.postfixOps
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.DataFrame

import it.gov.daf.ingestion.model._

object GenericTransformer {

  def apply(normalizer: DataTransformation)
    (implicit config: Config): Transformer = new Transformer {

    def transform(formats: List[Format])
      (implicit spark: SparkSession): Transformation = { data =>

      val res: DataFrame = formats.foldLeft(data)(normalizer.apply)

      Right(res)
    }

  }

}
