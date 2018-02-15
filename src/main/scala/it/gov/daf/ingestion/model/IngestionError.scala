package it.gov.daf.ingestion.model

trait IngestionError extends Throwable with Product with Serializable {
  def error: String
  override def toString = error
}

case class AuthenticationError(error: String) extends IngestionError
