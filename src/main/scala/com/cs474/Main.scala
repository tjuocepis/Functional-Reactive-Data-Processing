package com.cs474

import java.io.File

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.actor.{PoisonPill, Props}
import akka.pattern.ask
import com.cs474.server.actor.ActorSystemContainer
import com.cs474.server.stream.{UserDataStreaming, UserRatingsDataStreaming}

import scala.io.StdIn

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

    val userDataStreamer = new UserDataStreaming(usersFile, userRatingsFile) // class object to stream user data
    userDataStreamer.analyzeUserData("2349")
    userDataStreamer.analyzeCityLocationData("siauliai,n/a,lithuania")

    //    val userRatingsDataStreaming = new UserRatingsDataStreaming(userRatingsFile)
    //    userRatingsDataStreaming.startStreaming()
/*
    def route: Route = {
      (path("user-data") & get) {
        parameter("user" ? "all") { (user) =>

          userDataStreamer.startStreaming()
          complete("")
        }
      }
    }

    // START SERVER
    val bindingFuture = Http().bindAndHandle(route, "localhost", 9999)

    println("ENTER to terminate")
    StdIn.readLine()
    system.terminate()
*/
  }
}
