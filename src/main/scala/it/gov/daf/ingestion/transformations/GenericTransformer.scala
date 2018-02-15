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

package it.gov.daf.ingestion.transformations

import scala.language.postfixOps
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.DataFrame
import catalog_manager.yaml.DatasetCatalog

object GenericTransformer {

  def apply(normalizer: DataTransformation, transformationName: String): Transformer = new Transformer {

    val name = transformationName

    def transform(catalog: DatasetCatalog)(implicit spark: SparkSession): Transformation = {
      // TBD These columns will be derived from the catalog
      val columns = List("a", "b")
      transform(columns) (spark)
    }

    def transform(columns: List[String])
      (implicit spark: SparkSession): Transformation = { data =>

      val res = columns.foldLeft(data)(normalizer.apply )

      Right(res)
    }

  }

}
