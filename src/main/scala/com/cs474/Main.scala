package com.cs474

import java.io.File

import com.cs474.actor.ActorSystemContainer
import com.cs474.stream.{UserDataStreaming, UserRatingsDataStreaming}

/**
  * Main
  */
object Main {

  /**
    * Main function of program
    *
    * @param args command line arguments
    */
  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystemContainer.getInstance().getSystem
    implicit val materializer = ActorSystemContainer.getInstance().getMaterializer

    val usersFile = new File("data/BX-Users.csv")
    val userRatingsFile = new File("data/BX-Book-Ratings.csv")

    val userDataStreamer = new UserDataStreaming(usersFile) // class object to stream user data
    userDataStreamer.startStreaming() // starts streaming user data

    val userRatingsDataStreaming = new UserRatingsDataStreaming(userRatingsFile)
    userRatingsDataStreaming.startStreaming()
  }
}
