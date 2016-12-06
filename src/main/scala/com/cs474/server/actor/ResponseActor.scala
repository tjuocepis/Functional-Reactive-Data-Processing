package com.cs474.server.actor

import akka.actor.{Actor, ActorLogging}
import com.cs474.server.cases._
import com.cs474.server.stream.DataStream

/**
  * Receives messages from Actor created in Main and Actor Subscribers and sends results back to Actor in Main
  */
class ResponseActor extends Actor with ActorLogging {

  var responseCounter = 0 // Counts the number of responses received from subscribers
  var fullAnalysis: String = "[Displaying Analysis]\n"

  override def receive: Receive = {

    // When message is received to start user ratings stream for analysis
    case StartUserAnalysis(userId) =>
      val dataStream = new DataStream
      dataStream.analyzeUserData(userId, sender)

    // When message is received to start location stream for analysis
    case StartLocationAnalysis(location) =>
      val dataStream = new DataStream
      dataStream.analyzeLocationData(location, sender)

    // When message is received with location analysis
    case LocationAnalysis(analysis, actorRef) =>
      fullAnalysis = fullAnalysis.concat(analysis) // Combine responses into one full response
      responseCounter += 1
      if (responseCounter == 2 || analysis.isEmpty) { // When all the expected responses are received
        responseCounter = 0
        actorRef ! AnalysisResponse(fullAnalysis)
        fullAnalysis = "[Displaying Location Analysis]\n"
      }

    // When message is received with user ratings analysis
    case UserRatingsAnalysis(analysis, actorRef) =>
      fullAnalysis = fullAnalysis.concat(analysis) // Combine responses into one full response
      responseCounter += 1
      if (responseCounter == 3) { // When all the expected responses are received
        responseCounter = 0
        actorRef ! AnalysisResponse(fullAnalysis)
        fullAnalysis = "[Displaying User Ratings Analysis]\n"
      }
  }
}