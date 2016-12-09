package com.cs474

import java.io.File

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.cs474.server.actor.{ActorSystemContainer, ResponseActorContainer}
import com.cs474.server.cases.{AnalysisResponse, StartLocationAnalysis, StartUserAnalysis}
import com.cs474.server.stream.DataStream

import scala.concurrent.duration._
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

    implicit val system = ActorSystemContainer.actorSystem()
    implicit val materializer = ActorSystemContainer.materializer()
    implicit val timeout: Timeout = 5.seconds

    val usersFile = new File("data/BX-Users.csv")
    val userRatingsFile = new File("data/BX-Book-Ratings.csv")

    val userDataStreamer = new DataStream // class object to stream user data
    //userDataStreamer.analyzeUserData("2349")
    //userDataStreamer.analyzeLocationData("kiev,n/a,ukraine")

    def route: Route = {
      (path("ratings-data") & get) {
        parameter("user" ? "2349") { (user) =>

          onSuccess(ask(ResponseActorContainer.instance(), StartUserAnalysis(user)).mapTo[AnalysisResponse]) {
            case AnalysisResponse(analysis) =>
              complete(analysis)
          }
        }
      } ~
      (path("location-data") & get) {
        parameter("city" ? "vilnius", "state" ? "n/a", "country" ? "lithuania") { (city: String,state: String,country: String) =>

          val location = city+","+state+","+country
          onSuccess(ask(ResponseActorContainer.instance(), StartLocationAnalysis(location)).mapTo[AnalysisResponse]) {
            case AnalysisResponse(analysis) =>
              complete(analysis)
          }
        }
      }
    }

    // START SERVER
    val bindingFuture = Http().bindAndHandle(route, "localhost", 9999)

    println("ENTER to terminate")
    StdIn.readLine()
    system.terminate()
  }
}
