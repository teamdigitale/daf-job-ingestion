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

import cats._, cats.data._
import cats.implicits._
import monix.eval.Task
import io.circe.{ Decoder, Error, Encoder }
import com.softwaremill.sttp._
import com.softwaremill.sttp.circe._
import com.softwaremill.sttp.asynchttpclient.monix._
import scala.concurrent.duration._
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import it.gov.daf.ingestion.model._

package object services {

  type Service[A] = EitherT[Task, ServiceError, A]

  type RawService[A] = Task[Either[ServiceError, A]]

  implicit val backend = AsyncHttpClientMonixBackend()

  private def innerService[A: Decoder](uri: Uri, timeout: Duration = Inf)
    (implicit sttpAuth:PartialRequest[String, Nothing]): Task[Either[ServiceError, A]] =
    sttpAuth
      .get(uri)
      .response(asJson[A])
      .send().map(_.body).map(convertError)

  def serviceGet[A: Decoder](uri: Uri)
    (implicit sttpAuth:PartialRequest[String, Nothing]): Service[A] = EitherT(innerService(uri))

  def servicePost[A: Decoder, B: Encoder](uri: Uri, body: B)
    (implicit sttpAuth: PartialRequest[String, Nothing]): Service[A] =
    EitherT(sttpAuth
      .post(uri)
      .body(body)
      .response(asJson[A])
      .send().map(_.body).map(convertError))

  def serviceGetWithTimeout[A](timeout: FiniteDuration = 5.minutes)(service: Uri => RawService[A]): Uri => RawService[A] =
    uri => service(uri).timeoutTo(timeout, Task.now(Left(TimeoutError(s"Timeout for service call [$uri]"))))

  def serviceGetEnsure[A: Decoder](uri: Uri, condition: (A) => Boolean, delay: FiniteDuration = 5.seconds)
    (implicit sttpAuth:PartialRequest[String, Nothing]): Task[Either[ServiceError, A]] = {
    val task = innerService(uri).delayExecution(delay)
    task.flatMap {
      case r@Left(_)                  => Task.now(r)
      case r@Right(a) if condition(a) => Task.now(r)
      case _                          => serviceGetEnsure(uri, condition)}
  }

  def convertError[A](err: Either[String, Either[Error,A]]): Either[ServiceError, A] = {
    err match {
      case Left(error)        => Left(EndpointError(error))
      case Right(Left(error)) => Left(ResponseError(error))
      case Right(Right(a))    => Right(a)
    }
  }

  // todo: Improve this
  def future2Task[A](future: => Future[A])(implicit ec: ExecutionContext): Service[A] =
    EitherT(Task.deferFuture(future.map(Either.right)))

}
