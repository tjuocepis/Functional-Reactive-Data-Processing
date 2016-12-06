package com.cs474.server.cases

/**
  * Case class that is used as a message to ResponseActor so it can start streaming and analyzing user data
  *
  * @param userId User ID for which to do the data analysis
  */
case class StartUserAnalysis(userId: String)
