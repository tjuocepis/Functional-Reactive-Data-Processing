package com.cs474.server.actor

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.stream.scaladsl.Sink
import com.cs474.server.cases.{AnalysisResponse, LocationAnalysis, StartLocationAnalysis, StartUserAnalysis}
import com.cs474.server.stream.DataStream

import scala.concurrent.{Future, Promise}

/**
  * Created by titusjuocepis on 12/5/16.
  */
class ResponseActor extends Actor with ActorLogging {

  var locationResponseCounter = 0
  var fullAnalysis: String = "[Displaying Location Analysis]\n"

  override def receive: Receive = {

    case StartUserAnalysis(userId) =>
      val dataStream = new DataStream
      dataStream.analyzeUserData(userId, sender)

    case StartLocationAnalysis(location) =>
      val dataStream = new DataStream
      dataStream.analyzeLocationData(location, sender)

    case LocationAnalysis(analysis, actorRef) =>
      fullAnalysis = fullAnalysis.concat(analysis)
      locationResponseCounter += 1
      if (locationResponseCounter == 2) {
        locationResponseCounter = 0
        actorRef ! AnalysisResponse(fullAnalysis)
        fullAnalysis = "[Displaying Location Analysis]\n"
      }
  }
}
