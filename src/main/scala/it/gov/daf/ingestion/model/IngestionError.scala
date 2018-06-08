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

package it.gov.daf.ingestion.model

import io.circe.Error
import pureconfig.error.ConfigReaderFailures

trait IngestionError extends Throwable with Product with Serializable {
  def error: String
  override def toString = error
}

case class ConfigError(configError: ConfigReaderFailures) extends IngestionError {
  def error = configError.head.description
}

case class AuthenticationError(error: String) extends IngestionError

sealed trait ServiceError extends IngestionError

case class EndpointError(error: String) extends ServiceError

case class ResponseError(parseError: Error) extends ServiceError {
  val error = parseError.toString
}

case class TimeoutError(error: String) extends ServiceError

case class GenericError(error: String) extends ServiceError
