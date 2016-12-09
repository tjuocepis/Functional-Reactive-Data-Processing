package com.cs474.server.stream.flows

import akka.stream.{ActorAttributes, Supervision}
import akka.stream.scaladsl.Flow
import com.cs474.server.actor.ActorSystemContainer
import com.cs474.server.cases.{Location, User}
import com.cs474.server.util.SP

/**
  * Contains Flows for processing user data
  */
object UserDataFlows {

  implicit val system = ActorSystemContainer.actorSystem()

  /**
    * Takes in single user data strings and creates a User instance
    * The passed in array contains user info that came from splitting user data string
    *
    * @param cols Array containing single user info
    * @return Single User instance
    */
  def csvLinesArrayToUser(cols: Array[String]) = {
    val locSplit = SP.ridQuotes(cols(1)).split(",").map(_.trim)
    val location = Location(locSplit(0), locSplit(1), locSplit(2))
    cols(2) match {
      case "NULL" => User(SP.ridQuotes(cols(0)), location, 0) // If string contains NULL we place 0 for age and return User
      case _ => User(SP.ridQuotes(cols(0)), location, SP.ridQuotes(cols(2)).toInt) // Else we return User
    }
  }

  /**
    * Splits the incoming user strings and maps the results to a function that converts the data to User instances
    *
    * @return User instances
    */
  def csvToUserFlow = Flow[String].map(_.split(";").map(_.trim)).map(csvLinesArrayToUser)
    .withAttributes(ActorAttributes.supervisionStrategy {
      e: Throwable =>
        // system.log.error("Error parsing row event: {}", e)
        Supervision.Resume // skips erroneous data and resumes the stream
    })
}