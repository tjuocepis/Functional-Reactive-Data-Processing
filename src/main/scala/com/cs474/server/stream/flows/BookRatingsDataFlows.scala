package com.cs474.server.stream.flows

import akka.stream.{ActorAttributes, Supervision}
import akka.stream.scaladsl.Flow
import com.cs474.server.actor.ActorSystemContainer
import com.cs474.server.cases.UserBookRating
import com.cs474.server.util.SP

/**
  * Contains Flows for processing book ratings
  */
object BookRatingsDataFlows {

  implicit val system = ActorSystemContainer.getInstance().getSystem

  /**
    * Collects UserBookRating instances into a Sequence
    *
    * @return Sequence of UserBookRating instances
    */
  def userBookRatingsToSeq = Flow[UserBookRating].grouped(10000)

  /**
    * Takes in single book rating strings and creates a UserBookRating instance
    * The passed in array contains book rating info that came from splitting book rating data string
    *
    * @param cols Array containing single rating info
    * @return Single UserBookRating instance
    */
  def csvLinesArrayToUserBookRating(cols: Array[String]) = {
    UserBookRating(SP.ridQuotes(cols(0)), SP.ridQuotes(cols(1)), SP.ridQuotes(cols(2)).toInt)
  }

  /**
    * Splits the incoming ratings strings and maps the results to a function that converts the data
    * to UserBookRating instances
    *
    * @return UserBookRating instances
    */
  def csvToUserBookRatingFlow = Flow[String].map(_.split(";").map(_.trim)).map(csvLinesArrayToUserBookRating)
    .withAttributes(ActorAttributes.supervisionStrategy {
      e: Throwable =>
        system.log.error("Error parsing row event: {}", e)
        Supervision.Resume // skips erroneous data and resumes the stream
    })
}
