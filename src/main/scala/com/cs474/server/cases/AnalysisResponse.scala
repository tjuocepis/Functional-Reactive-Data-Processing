package com.cs474.server.cases

/**
  * Case class Actor message used to send data analysis to ResponseActor
  *
  * @param analysis Data analysis string
  */
case class AnalysisResponse(analysis: String)
