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

// import com.typesafe.config.Config
import org.apache.spark.sql.SparkSession

import scala.language.postfixOps
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.broadcast.Broadcast
import it.gov.daf.ingestion.model._


import scala.math.min

case class TagOntology(colName: String, hierarchy: String, vocName: String, ontoTag: Map[String, String]) extends Serializable

object Standardization extends Transformer[StdFormat] {

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
    scorList.length match {
      case 0 =>
        println(s"stringIn: $stringIn")
        (stringIn, "astringsocomplexthelevenshteindistanceis9999", 9999)
      case _ => scorList(0)
    }
    // scorList(0)

  }

  val addedColVal: String = "__std_"
  val addedColStat: String = "__stdscore_"

  val name = "std_voc"


  def transform(columns: List[StdFormat])(implicit spark: SparkSession): Transformation = {

    // TODO Order colStd using the number of times a voc is used.

    transform(columns, "ciao", spark)
  }

  def transform(stdInfoList: List[StdFormat], test: String,
    spark: SparkSession): Transformation = { data =>

    println("Std - transform")

    //Calculate involved Vocabularies, calculate occurrencies, and remove duplicate
    val dicInvolved = stdInfoList.map(x => (x.colInfo.vocUri, x.colInfo.vocPath))
        .groupBy(x=>x).map(x=>(x._1, x._2.size))


    //Broadcast involved vocabulary
    //TODO think to optimize it to have broadcasted only the voc that is actually used in the loop, and load the new ones as needed
    implicit val encoder = RowEncoder(data.schema)
    val vocLoc: Map[String, Array[Row]] = dicInvolved.map { dic =>
      dic._1._1 ->  spark.read.parquet(dic._1._2).collect
    }.toMap
    val vocs: Broadcast[Map[String,Array[Row]]]  = spark.sparkContext.broadcast(vocLoc)

    //Get the ordered list of column and info from which to start the standardization procedure
    val stdInfoListOrdered = stdInfoList.sortBy(x=> (dicInvolved((x.colInfo.vocUri, x.colInfo.vocPath)), x.colInfo.vocUri, x.colInfo.propHierarchy.size)).to[List]
    // println(s"stdInfoListOrdered: $stdInfoListOrdered")

    def standardize(data: DataFrame, stdInfo: StdFormat): DataFrame = {
      // println("Std - standardize: " + stdInfo)

      //Prepare Info needed for the procedure

      //Get the name of the column to standardize
      val colName2Std = stdInfo.column

      //Get the hierarchy of the corresponding voc column
      val hierarchy = stdInfo.colInfo.propHierarchy.toList

      //Get the voc property related to the column 2 std
      val vocProp = stdInfo.colInfo.vocProp

      //Get the colGroup keyword
      val colGroup = stdInfo.colInfo.colGroup

      //Get the involved vocabulary
      val voc = vocs.value(stdInfo.colInfo.vocUri)


      //Transform this in Map for easiest use in the procedure
      val stdInfoMap = stdInfoListOrdered.map(x => (x.column -> x.colInfo)).toMap

      //Get the list of columns that have been already standardized and are in the hierarchy of the element we want to standardize
      val colLinkedNames: List[String] =
        data.columns
          .filter(x => x.startsWith(addedColVal))
          .map(x => x.replace(addedColVal, "")).toList
          .filter { colNameLinked =>
            //Look if the vocProp of the linked column is contained in the hierarchy of the column we want to standardize, and share the same colGroup keyword
            val infoColLinked = stdInfoMap.get(colNameLinked)

            infoColLinked match {
              case Some(s) =>
                s.colGroup.equals(colGroup) &&
                  hierarchy.map(x=>x.split(',')).flatten[String].filter(x=> !x.equals(vocProp)).contains(s.vocProp)

              case _ => false
            }

          }

      //columns to be selected from df_data (the col to be std and the correlated ones that have been already standardized)
      val colSelect_data: List[String] = colName2Std :: colLinkedNames

      //columns to be selected from the vocabulary
      val colSelect_voc: Columns = colSelect_data.flatMap { colName =>
        stdInfoMap.get(colName).map(_.vocProp)
        // val infoColSel: Option[StdColInfo] = stdInfoMap.get(colName)
        // infoColSel.map(_.vocProp)
      }

      import spark.implicits._

      //Create the appropriate structure for the vocabulary
      val voc_restr: Array[List[String]] = voc.map { row =>
        colSelect_voc.map{col =>
          row.getAs[String](col)
        }
      }


      //produce the dataframe with the result of stdInference function (standardized col + score)
      val df_conv = data.select(colSelect_data.head, colSelect_data.tail.map(addedColVal + _): _*).distinct.map { x =>

        val xSeq: Seq[String] = x.toSeq.map(x=> x.asInstanceOf[String])
        //println(s"xSeq: ${xSeq.mkString("|")}")


        if (xSeq.length > 1) {

          val dataLinkedCol: Seq[String] = xSeq.tail.map(_.toLowerCase)
          val voc_cust = voc_restr.filter( x => x.map(_.toLowerCase).tail == dataLinkedCol).map(_.head)
          doit("levenshtein")(xSeq.head, voc_cust)

        } else {

          val voc_cust = voc_restr.map(x=>x(0).toString)
          doit("levenshtein")(xSeq.head, voc_cust)

        }
      }

      data.join(df_conv.toDF(colName2Std, addedColVal + colName2Std, addedColStat + colName2Std), Seq(colName2Std), "left")

    }

    Right(stdInfoListOrdered.foldLeft(data) {standardize _})

  }


}
