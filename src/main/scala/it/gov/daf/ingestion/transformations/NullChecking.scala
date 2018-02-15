package it.gov.daf.ingestion.transformations

import scala.language.postfixOps
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.DataFrame
import catalog_manager.yaml.DatasetCatalog
import org.apache.spark.sql.functions._

object NullChecking extends Transformer {

  val name = "norm_null"

  def transform(catalog: DatasetCatalog)(implicit spark: SparkSession): Transformation = {
    // TBD These columns will be derived from the catalog
    val columns = List("a", "b")
    transform(columns)(spark)
  }

  private def normalizeCol(data: DataFrame, colName: String): DataFrame =
    data.withColumn(colName, when(col(colName) === "", null).otherwise(col(colName)))

  def transform(columns: List[String])
    (implicit spark: SparkSession): Transformation = { data =>

    val res = columns.foldLeft(data)(normalizeCol _ )

    Right(res)
  }

}
