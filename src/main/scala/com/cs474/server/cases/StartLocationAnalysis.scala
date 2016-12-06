package com.cs474.server.cases

/**
  * Case class that is used as a message to start streaming and analyzing location data
  *
  * @param location Location on which to do the analysis
  */
case class StartLocationAnalysis(location: String)
