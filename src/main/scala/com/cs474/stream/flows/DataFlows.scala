package com.cs474.stream.flows

import akka.actor.ActorSystem
import akka.stream.{ActorAttributes, Supervision}
import akka.stream.scaladsl.Flow
import com.cs474.actor.ActorSystemContainer
import com.cs474.cases.{Location, User, UserBookRating}
import com.cs474.util.StringProcessor

import scala.util.Try

/**
  * Created by titusjuocepis on 12/2/16.
  */

sealed trait Flows {

  trait DataFlows {

    val system: ActorSystem

    def averageFlow[T] = Flow[T]

    def averagePerAttributeFlow[T] = Flow[T]

    def filterByAttributeFlow[T](attr: String) = Flow[T]

  }

  trait BookRatingDataFlow {

    def csvToUserBookRating = Flow[String]

    def csvLinesArrayToUserBookRating(line: Array[String])
  }

  trait UserDataFlow {

    def csvToUser = Flow[String]

    def csvLinesArrayToUser(line: Array[String])

  }
}

object DataFlows extends Flows {

  val system = ActorSystemContainer.getInstance().getSystem

  /**
    * Calculates the average user age for the entire data set by applying a fold operation
    * that accumulates all the ages into a variable and counts the number of users that had
    * their age provided and all the users in the data set
    *
    * @return returns number of users with provided age, total age accumulated and total number of users in data set
    */
  def averageFlow[T] = Flow[T].fold((0, 0.0, 0)) {
    (nums: (Int, Double, Int), data: T) =>

      var dataConvertedFromString: Option[Try[Int]] = None

      data match {
        case user: User =>
          dataConvertedFromString = Some(Try(StringProcessor.removeQuotes(user.age).toInt))
        case bookRating: UserBookRating =>
          dataConvertedFromString = Some(Try(StringProcessor.removeQuotes(bookRating.rating).toInt))
      }

      var validDataCounter: Option[Int] = None
      var dataAccumulator: Option[Double] = None
      var totalDataCounter = nums._3

      if (dataConvertedFromString.get.isSuccess && dataConvertedFromString.get.get != 0) {
        validDataCounter = Some(nums._1 + 1)
        dataAccumulator = Some(nums._2 + dataConvertedFromString.get.get)
      }
      else {
        validDataCounter = Some(nums._1 + 0)
        dataAccumulator = Some(nums._2 + 0)
      }

      totalDataCounter = nums._3 + 1

      (validDataCounter.get, dataAccumulator.get, totalDataCounter)

  }.withAttributes(ActorAttributes.supervisionStrategy {
    e: Throwable =>
      system.log.error("Error calculating average user age: {}", e)
      Supervision.Resume
  })

  /**
    * Calculates the average user age for a specific location by applying a fold operation
    * that accumulates all the ages into a variable and counts the number of users that had
    * their age provided and all the users in from the specified location
    *
    * @return returns the location, number of users with provided age, total age accumulated
    *         and total number of users in data set
    */
  def averagePerAttributeFlow[T] = Flow[T]
    .groupBy(10000, {
      case User(id, location, age) => location
      case UserBookRating(userId, bookISBN, rating) => userId
    })
    .fold(("", 0, 0.0, 0)) {
      (nums: (String, Int, Double, Int), data: T) =>

        var dataConvertedFromString: Option[Try[Int]] = None

        data match {
          case user: User =>
            dataConvertedFromString = Some(Try(StringProcessor.removeQuotes(user.age).toInt))
          case bookRating: UserBookRating =>
            dataConvertedFromString = Some(Try(StringProcessor.removeQuotes(bookRating.rating).toInt))
        }

        var validDataCounter: Option[Int] = None
        var dataAccumulator: Option[Double] = None
        var totalDataCounter = nums._4

        if (dataConvertedFromString.get.isSuccess) {
          validDataCounter = Some(nums._2 + 1)
          dataAccumulator = Some(nums._3 + dataConvertedFromString.get.get)
        }
        else {
          validDataCounter = Some(nums._2 + 0)
          dataAccumulator = Some(nums._3 + 0)
        }

        totalDataCounter = nums._4 + 1

        data match {
          case user: User =>
            (user.location.toString, validDataCounter.get, dataAccumulator.get, totalDataCounter)
          case bookRating: UserBookRating =>
            (bookRating.userId, validDataCounter.get, dataAccumulator.get, totalDataCounter)
        }

    }.withAttributes(ActorAttributes.supervisionStrategy {
    e: Throwable =>
      system.log.error("Error calculating average user age per location: {}", e)
      Supervision.Resume
  }).mergeSubstreams

  /**
    * Filters out ratings and keeps only the ones that matched the provided userId
    *
    * @param attr location of users that we want to keep
    * @return returns the filtered users
    */
  def filterByAttributeFlow[T](attr: String) = Flow[T].filter( {
    case user: User =>
      println(">>>>>>>>>> " + user.location.toString)
      user.location.toString.contentEquals(attr) // check if location matches
    case bookRating: UserBookRating =>
      StringProcessor.removeQuotes(bookRating.userId).contentEquals(attr) // check if userId matches
  }).withAttributes(ActorAttributes.supervisionStrategy {
    e: Throwable =>
      system.log.error(s"Error filtering ratings by '$attr': {}", e)
      Supervision.Resume // skips the erroneous data and resumes the stream
  })

  /**
    * Splits the incoming strings and maps the results to a User case class
    *
    * @return returns the mapped User case class
    */
  def csvToUser = Flow[String].map(_.split(";").map(_.trim)).map(csvLinesArrayToUser)
    .withAttributes(ActorAttributes.supervisionStrategy {
      e: Throwable =>
//        system.log.error("Error parsing row event: {}", e)
        Supervision.Resume // skips erroneous data and resumes the stream
    })

  /**
    * Splits the incoming strings and maps the results to a UserBookRating case class
    *
    * @return returns the mapped UserBookRating case class
    */
  def csvToUserBookRating = Flow[String].map(_.split(";").map(_.trim)).map(csvLinesArrayToUserBookRating)
    .withAttributes(ActorAttributes.supervisionStrategy {
      e: Throwable =>
        system.log.error("Error parsing row event: {}", e)
        Supervision.Resume // skips erroneous data and resumes the stream
    })

  /**
    * Takes in the split string values and places them to the corresponding User case class data
    *
    * @param line array containing the split data
    * @return returns User case class with data
    */
  def csvLinesArrayToUser(line: Array[String]) = {
    val locSplit = StringProcessor.removeQuotes(line(1)).split(",")
    val location = Location(locSplit(0), locSplit(1), locSplit(2))
    User(line(0), location, line(2))
  }

  /**
    * Takes in the split string values and places them to the corresponding UserBookRating case class data
    *
    * @param line array containing the split data
    * @return returns UserBookRating case class with data
    */
  def csvLinesArrayToUserBookRating(line: Array[String]) = UserBookRating(line(0), line(1), line(2))
}
