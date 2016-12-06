package com.cs474.server.actor

import akka.actor.Props

/**
  * Singleton instance that holds the ResponseActor
  */
object ResponseActorContainer {

  /**
    * ResponseActor instance
    */
  private val _instance = ActorSystemContainer.getInstance().getSystem.actorOf(Props(new ResponseActor))

  /**
    * Returns the ResponseActor instance
    *
    * @return ResponseActor instance
    */
  def instance() =
    _instance
}
