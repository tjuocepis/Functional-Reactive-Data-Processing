package com.cs474.server.actor

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

/**
  * Created by titusjuocepis on 12/9/16.
  */
object ActorSystemContainer {

  private val _actorSystem = ActorSystem.create("data-streaming")
  private val _materialzer = ActorMaterializer.create(_actorSystem)

  def actorSystem() = {
    _actorSystem
  }

  def materializer() = {
    _materialzer
  }
}
