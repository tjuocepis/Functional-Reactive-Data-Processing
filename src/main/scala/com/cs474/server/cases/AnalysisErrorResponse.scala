package com.cs474.server.cases

/**
  * Case class Actor message used to send error response to ResponseActor
  *
  * @param error String containing the error
  */
case class AnalysisErrorResponse(error: String)
