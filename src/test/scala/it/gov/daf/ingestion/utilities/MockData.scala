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

package it.gov.daf.ingestion.utilities

import org.apache.spark.sql.types.{ StructField, StructType, StringType, IntegerType }

import it.gov.daf.ingestion.model._

object MockData {

  val expectedAddress = List(("piazza della vergine Immacolata 00144 Roma (ROMA) Lazio Italia", """[
  {
    "label" : "road",
    "value" : "piazza della vergine"
  },
  {
    "label" : "city",
    "value" : "immacolata"
  },
  {
    "label" : "postcode",
    "value" : "00144"
  },
  {
    "label" : "city",
    "value" : "roma"
  },
  {
    "label" : "state_district",
    "value" : "roma"
  },
  {
    "label" : "state",
    "value" : "lazio"
  },
  {
    "label" : "country",
    "value" : "italia"
  }
]""","piazza della vergine","immacolata","00144","roma","italia"),
("via piave, 25 Rocca di papa (RM)","""[
  {
    "label" : "road",
    "value" : "via piave"
  },
  {
    "label" : "house_number",
    "value" : "25"
  },
  {
    "label" : "city",
    "value" : "rocca di papa"
  },
  {
    "label" : "state_district",
    "value" : "rm"
  }
]""","via piave","rocca di papa","","rm",""),
("Corso V. Emanuele, 2 Sant'angelo dei lombardi Roccapriora 00100 Italia","""[
  {
    "label" : "road",
    "value" : "corso v. emanuele"
  },
  {
    "label" : "house_number",
    "value" : "2"
  },
  {
    "label" : "city",
    "value" : "sant'angelo dei lombardi roccapriora"
  },
  {
    "label" : "postcode",
    "value" : "00100"
  },
  {
    "label" : "country",
    "value" : "italia"
  }
]""","corso v. emanuele","sant'angelo dei lombardi roccapriora","00100","","italia"))

  val dateSchema =
      StructType(List(StructField("value",StringType,true),
        StructField("__date_value",StringType,true),
        StructField("__date_year_value",IntegerType,true),
        StructField("__date_month_value",IntegerType,true),
        StructField("__date_day_value",IntegerType,true),
        StructField("__date_week_value",IntegerType,true),
        StructField("__date_dayofyear_value",IntegerType,true)))

  val expectedPipeline = Pipeline("daf://voc/ECON__impresa_contabilita/ds_aziende",
      "/daf/voc/ECON__impresa_contabilita/ds_aziende",
    None,List(
      IngestionStep("values to null",1,
        List(NullFormat("indirizzo_sede1",List(""," ")),
          NullFormat("indirizzo_sede2",List(""," ")),
          NullFormat("data_costituzione",List(""," " )),
          NullFormat("sito",List(""," " )))),
      IngestionStep("date to ISO8601",1,
        List(DateFormat("data_costituzione","dd/MM/yyyy"))),
      IngestionStep("url normalizer",1,
        List(UrlFormat("sito"))),
      IngestionStep("address normalizer",1,
        List(AddressFormat("indirizzo_sede1"),
          AddressFormat("indirizzo_sede2"))),
      IngestionStep("standardization",1,
        List(
          StdFormat("citta_sede1",
            StdColInfo("daf://voc/REGI__europa/voc_territorial_classification",
              "src/test/resources/territorial-classification-adj",
              "Denominazione_italiano",
              List("Codice_Ripartizione_Geografica","Ripartizione_geografica",
                "Codice_Regione","Denominazione_regione", "Codice_Citta_Metropolitana",
                "Denominazione_Citta_metropolitana", "Codice_Provincia",
                "Denominazione_provincia", "Codice_Comune_formato_alfanumerico",
                "Denominazione_italiano","Denominazione_tedesco","Codice_Comune_formato_numerico",
                "Codice_Comune_numerico__110_province_2010_2016",
                "Codice_Comune_numerico_107_province_2006_2009","Codice_Comune_numerico_103_province_1995_2005",
                "Codice_Catastale_comune"),
              "sede1")),
          StdFormat("provincia_sede1",
            StdColInfo("daf://voc/REGI__europa/voc_territorial_classification",
              "src/test/resources/territorial-classification-adj",
              "Denominazione_provincia",
              List("Codice_Ripartizione_Geografica","Ripartizione_geografica",
                "Codice_Regione","Denominazione_regione",
                "Codice_Citta_Metropolitana","Denominazione_Citta_metropolitana",
                "Codice_Provincia","Denominazione_provincia"),
              "sede1")),
          StdFormat("regione_sede1",
            StdColInfo("daf://voc/REGI__europa/voc_territorial_classification",
              "src/test/resources/territorial-classification-adj",
              "Denominazione_regione",
              List("Codice_Ripartizione_Geografica","Ripartizione_geografica",
                "Codice_Regione","Denominazione_regione"),"sede1")),
          StdFormat("citta_sede2",
            StdColInfo("daf://voc/REGI__europa/voc_territorial_classification",
              "src/test/resources/territorial-classification-adj",
              "Denominazione_italiano",
              List("Codice_Ripartizione_Geografica","Ripartizione_geografica",
                "Codice_Regione","Denominazione_regione", "Codice_Citta_Metropolitana",
                "Denominazione_Citta_metropolitana", "Codice_Provincia",
                "Denominazione_provincia", "Codice_Comune_formato_alfanumerico",
                "Denominazione_italiano","Denominazione_tedesco",
                "Codice_Comune_formato_numerico","Codice_Comune_numerico__110_province_2010_2016",
                "Codice_Comune_numerico_107_province_2006_2009","Codice_Comune_numerico_103_province_1995_2005",
                "Codice_Catastale_comune"),
              "sede2")),
          StdFormat("provincia_sede2",
            StdColInfo("daf://voc/REGI__europa/voc_territorial_classification",
              "src/test/resources/territorial-classification-adj",
              "Denominazione_provincia",
              List("Codice_Ripartizione_Geografica","Ripartizione_geografica",
                "Codice_Regione","Denominazione_regione", "Codice_Citta_Metropolitana",
                "Denominazione_Citta_metropolitana", "Codice_Provincia","Denominazione_provincia"),
              "sede1")),
          StdFormat("regione_sede2",
            StdColInfo("daf://voc/REGI__europa/voc_territorial_classification",
              "/git/gruggiero/daf-job-ingestion/src/test/resources/territorial-classification-adj",
              "Denominazione_regione",
              List("Codice_Ripartizione_Geografica","Ripartizione_geografica",
                "Codice_Regione,Denominazione_regione"),
              "sede1")),
          StdFormat("licenza_label_1",
            StdColInfo("daf://voc/TECH__scienza/voc_licenze",
              "src/test/resources/licences",
              "label_level_1",
              List("code_level_1","label_level_1"),
              "")),
          StdFormat("licenza_label_2",
            StdColInfo("daf://voc/TECH__scienza/voc_licenze",
              "src/test/resources/licences",
              "label_level_2",
              List("code_level_1","label_level_1", "code_level_2","label_level_2"),
              ""))))))

      }