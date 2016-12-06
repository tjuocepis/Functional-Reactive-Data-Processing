package com.cs474.server.cases

import akka.actor.ActorRef

/**
  * Case class that holds user ratings data analysis also used as a message to send to ResponseActor
  *
  * @param analysis User ratings data analysis
  * @param actorRef Reference to the Actor created in Main that waits for a response
  *                 to complete an http route to the client
  */
case class UserRatingsAnalysis(analysis: String, actorRef: ActorRef)
