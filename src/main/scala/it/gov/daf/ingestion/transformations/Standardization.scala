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
import scala.language.postfixOps
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.{ DataFrame, Row }
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.broadcast.Broadcast
import it.gov.daf.ingestion.model.Pipeline
import it.gov.daf.ingestion.model._
import it.gov.daf.ingestion.transformations.DateTransformer._

import scala.math.min

case class TagOntology(colName: String, hierarchy: String, vocName: String, ontoTag: Map[String, String]) extends Serializable

object Standardization extends Transformer {

  type Columns = List[String]
  type OntoTag = Map[String, String]

  def doit(matchType: String)(stringIn: String, voc: Seq[String]): (String, String, Int) = {
    matchType match {
      case "levenshtein" => levenshtein(stringIn, voc)
      case _ => levenshtein(stringIn, voc)
    }
  }

  def levenshtein(stringIn: String, voc: Seq[String]): (String, String, Int) = {

    def editDist[A](a: Iterable[A], b: Iterable[A]) = {
      ((0 to b.size).toList /: a)((prev, x) =>
        (prev zip prev.tail zip b).scanLeft(prev.head + 1) {
          case (h, ((d, v), y)) => min(min(h + 1, v + 1), d + (if (x == y) 0 else 1))
        }) last
    }
    //get list of tuple (stringIn, stringTrans, score)
    val scorList = voc.map(x=> (stringIn, x, editDist(stringIn, x))).sortBy(_._3)
    scorList(0)

  }

  val addedColVal: String = "__std_"
  val addedColStat: String = "__stdscore_"

  val name = "std_voc"

  def transform(pipeline: Pipeline)(implicit spark: SparkSession): Transformation = {
    val ontologies = List(TagOntology("poiname","POI-AP_IT.PointOfInterestCategory.POIcategoryName","/user/giovanni/data/voc_poi",Map("POI-AP_IT_PointOfInterestCategory_POICategoryName" -> "POI-AP_IT.PointOfInterestCategory.POIcategoryName")))
    val dataOntology = Map("poiname" -> "POI-AP_IT.PointOfInterestCategory.POIcategoryName")
    transform(ontologies, dataOntology, spark)
  }


  def transform(columns: List[Format])(implicit spark: SparkSession): Transformation = {
    val ontologies = List(TagOntology("poiname","POI-AP_IT.PointOfInterestCategory.POIcategoryName","/user/giovanni/data/voc_poi",Map("POI-AP_IT_PointOfInterestCategory_POICategoryName" -> "POI-AP_IT.PointOfInterestCategory.POIcategoryName")))
    val dataOntology = Map("poiname" -> "POI-AP_IT.PointOfInterestCategory.POIcategoryName")
    transform(ontologies, dataOntology, spark)
  }

  def transform(ontologies: List[TagOntology],
    dataOntology: OntoTag,
    spark: SparkSession): Transformation = {

    data =>

    // the dataFrame is now a parameter
    // val data: DataFrame = spark.read.parquet(ddsUri).cache

    // import spark.implicits._

    implicit val encoder = RowEncoder(data.schema)

    val vocLoc: Map[String, Array[Row]] = ontologies.map { onto =>
      onto.vocName ->  spark.read.parquet(onto.vocName).collect
    }.toMap

    // broadcast all the vocabolaries
    val vocs: Broadcast[Map[String,Array[Row]]]  = spark.sparkContext.broadcast(vocLoc)

    def standardize(data: DataFrame, onto: TagOntology): DataFrame = {
      import onto._
      val voc = vocs.value(vocName)

      // val entityLinked = hierarchy.filter( _ != tag)

      val colLinkedNames: List[String] =
        data.columns
          .filter(x => x.startsWith(addedColVal))
          .map(x => x.replace(addedColVal, "")).toList
          .filter { x =>
            val colTag = dataOntology.get(x)
            colTag.fold(false){ s =>
              val endIndex = s.lastIndexOf(".")
              val entity = s.substring(0,endIndex)
              hierarchy == entity
            }
          }

      //columns to be selected from df_data (the col to be std and the correlated ones that have been already standardized)
      val colSelect_data: List[String] = colName :: colLinkedNames

      val colSelect_voc: Columns = colSelect_data.flatMap { x =>
        val colTag = dataOntology.get(x)
        colTag.flatMap { s =>
          ontoTag.find { x => (x._2).equals(s) }.map(x=>x._1)
        }
      }

      import spark.implicits._

      //produce the dataframe with the result of stdInference function (standardized col + score)
      val df_conv = data.select(colSelect_data.head, colSelect_data.tail: _*).distinct.map { x =>
        val xSeq = x.toSeq.map(x=> x.asInstanceOf[String])
        if (xSeq.length >1) {
          val dataLinkedCol = xSeq.tail.map(x=>x.asInstanceOf[String].toLowerCase)
          val voc_cust = voc.filter(x=> x.toSeq.map(x=>x.asInstanceOf[String].toLowerCase).tail.equals(dataLinkedCol)).map(x=>x(0).asInstanceOf[String])
          doit("levenshtein")(xSeq.head, voc_cust)
        } else {
          val voc_cust = voc.map(x=>x(0).asInstanceOf[String])
          println(s"voc_cust: ${voc_cust.mkString(",")}")

          doit("levenshtein")(xSeq.head, voc_cust)
        }
      }

      data.join(df_conv.toDF(colName, addedColVal + colName, addedColStat + colName), Seq(colName), "left")
    }

    Right(ontologies.foldLeft(data){ standardize _ })

  }


}
