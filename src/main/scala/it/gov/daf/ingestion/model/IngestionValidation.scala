package it.gov.daf.ingestion.model

sealed trait IngestionValidation extends Product with Serializable {
  def message: String
}

// TODO define better messages
case object StandardizationError extends IngestionValidation {
  def message: String = "Something went wrong."
}
