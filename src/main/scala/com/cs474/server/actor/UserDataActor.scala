package com.cs474.server.actor

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}
import com.cs474.server.cases.UserAnalysis

/**
  * Created by titusjuocepis on 12/3/16.
  */
class UserDataActor extends Actor with ActorLogging {

  override def receive: Receive = {

    case UserAnalysis =>
  }
}
