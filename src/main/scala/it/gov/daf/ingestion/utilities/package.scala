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

package it.gov.daf.ingestion

import cats.syntax.either._
import scala.concurrent.{Future,Promise}
import java.util.Base64
import it.gov.daf.ingestion.model.User

package object utilities {

  def Either2Future[T](e: Either[Any, T]): Future[T] = {
    val p = Promise[T]

    val dummy = e match {
      case Right(v)  => p.success(v)
      case Left(err: Exception) => p.failure(err)
      case Left(err) => p.failure(new Exception(err.toString))
    }
    p.future
  }

  def Option2Future[T](o: Option[T]): Future[T] = {
    val p = Promise[T]
    o.fold(p.failure(new Exception("Mandatory field missing")))(t => p.success(t)).future
  }

  // TODO Move to common
  def Option2Either[L, R](t: => L)(opt: Option[R]): Either[L, R] = opt match {
    case Some(e) => Either.right(e)
    case _ => Either.left(t)
  }

  def user2auth(user: User) = {
    val plainCreds = s"${user.login}:${user.password}"
    val plainCredsBytes = plainCreds.getBytes
    val base64CredsBytes = Base64.getEncoder.encode(plainCredsBytes)
    val base64Creds = new String(base64CredsBytes)
    s"Basic $base64Creds"
  }

}
