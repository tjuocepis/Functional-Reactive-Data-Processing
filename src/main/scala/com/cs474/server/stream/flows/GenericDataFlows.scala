package com.cs474.server.stream.flows

import akka.actor.ActorSystem
import akka.stream.{ActorAttributes, Supervision}
import akka.stream.scaladsl.Flow
import com.cs474.server.actor.ActorSystemContainer
import com.cs474.server.cases.{Location, User, UserBookRating}
import com.cs474.server.util.SP

import scala.util.Try

/**
  * Contains generic Flows that can analyze user data and book ratings data
  */
object GenericDataFlows {

  implicit val system = ActorSystemContainer.getInstance().getSystem

  /**
    * Generic Flow that analyzes data for an entire data set by applying a fold operation that counts number of valid
    * data points, accumulates the data and counts the number of all data points
    *
    * @return Number of valid data points, accumulated data and number of all data points as a tuple
    */
  def analyzeDataFlow[T] = Flow[T].fold((0, 0.0, 0)) {
    (nums: (Int, Double, Int), incomingData: T) =>

      var data: Option[Int] = None

      // Check to see if working with User or UserBookRating
      incomingData match {
        case user: User =>
          data = Some(user.age)
        case bookRating: UserBookRating =>
          data = Some(bookRating.rating)
      }

      var validDataCounter = nums._1
      var dataAccumulator = nums._2
      var totalDataCounter = nums._3

      // If 0 means data is missing or not valid
      if (data.getOrElse(-1) != 0) {
        validDataCounter = nums._1 + 1
        dataAccumulator = nums._2 + data.getOrElse(0)
      }

      totalDataCounter = nums._3 + 1

      (validDataCounter, dataAccumulator, totalDataCounter)

  }.withAttributes(ActorAttributes.supervisionStrategy {
    e: Throwable =>
      system.log.error("Error calculating average user age: {}", e)
      Supervision.Resume
  })

  /**
    * Generic Flow that analyzes data for specific attribute by applying a fold operation that counts number of valid
    * data points, accumulates the data and counts the number of all data points
    *
    * @return Attributed that data was analyzed by, number of valid data points, accumulated data and number of all
    *         data points as a tuple
    */
  def analyzeDataForAttributeFlow[T] = Flow[T]
    .groupBy(10000, {
      case User(id, location, age) => // If working with User
        location match {
          case Location(city,"n/a",country) => // If client wants to analyze by city and country (Some cities don't belong to a state)
          case Location("n/a",state,country) => // If client wants to analyze by state and country
          case Location("n/a", "n/a",country) => // If client wants to analyze by country only
          case Location(city,state,country) => // If client wants to analyze by city, state, and country
        }
      case UserBookRating(userId, bookISBN, rating) => userId // If working with UserBookRating
    })
    .fold(("", 0, 0.0, 0)) {
      (nums: (String, Int, Double, Int), incomingData: T) =>

        var data: Option[Int] = None

        // Check to see if working with User or UserBookRating
        incomingData match {
          case user: User =>
            data = Some(user.age)
          case bookRating: UserBookRating =>
            data = Some(bookRating.rating)
        }

        var validDataCounter = nums._2
        var dataAccumulator = nums._3
        var totalDataCounter = nums._4

        // If 0 means data is missing or not valid
        if (data.getOrElse(-1) != 0) {
          validDataCounter = nums._2 + 1
          dataAccumulator = nums._3 + data.getOrElse(0)
        }

        totalDataCounter = nums._4 + 1

        // Check to see if we should return User analysis data or UserBookRating analysis data
        incomingData match {
          case user: User =>
            (user.location.toString, validDataCounter, dataAccumulator, totalDataCounter)
          case bookRating: UserBookRating =>
            (bookRating.userId, validDataCounter, dataAccumulator, totalDataCounter)
        }

    }.withAttributes(ActorAttributes.supervisionStrategy {
    e: Throwable =>
      system.log.error("Error calculating average user age per location: {}", e)
      Supervision.Resume
  }).mergeSubstreams

  /**
    * Generic Flow that filters out data by provided attribute
    *
    * @param attr Attribute to filter by
    * @return Filtered data
    */
  def filterByAttributeFlow[T](attr: String) = Flow[T].filter( {
    case user: User => // If working with User
      val attrSplit = attr.split(",").map(_.trim())
      attrSplit match {
        case Array(city,"n/a",country) => // If filtering by city and country only
          user.location.isStatelessCity(city, country) // Check for match by calling specific method if no state provided
        case Array("n/a","n/a",country) => // If filtering by country only
          user.location.isCountry(country) // Check for matche by calling specific method if only contry is provided
        case Array("n/a",state,country) => // If filtering by state and country
          user.location.isState(state,country) // Check for matche by calling specific method if only state and country is provided
        case Array(city,state,country) => // If filtering by city, state and country
          user.location.isCity(city, state, country) // Check for match by calling specific method if all three are provided
      }
    case bookRating: UserBookRating => // If working with UserBookRating
      bookRating.userId.contentEquals(attr) // Check if userId matches the attribute we want to filter by
  }).withAttributes(ActorAttributes.supervisionStrategy {
    e: Throwable =>
      system.log.error(s"Error filtering ratings by '$attr': {}", e)
      Supervision.Resume // skips the erroneous data and resumes the stream
  })
}